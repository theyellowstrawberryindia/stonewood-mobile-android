package uk.co.savills.stonewood.screen.datatransfer

import android.app.Application
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.PHOTO_DIRECTORY
import uk.co.savills.stonewood.model.survey.ImageRequestModel
import uk.co.savills.stonewood.model.survey.datatransfer.AlterationModel
import uk.co.savills.stonewood.model.survey.datatransfer.DataTransferRequestModel
import uk.co.savills.stonewood.model.survey.datatransfer.DataTransferResponseModel
import uk.co.savills.stonewood.model.survey.entry.EnergySurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.HHSRSSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.QualityStandardSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.RiskAssessmentSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.StockSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.model.survey.property.PropertyStatus
import uk.co.savills.stonewood.network.exception.NetworkUnavailableException
import uk.co.savills.stonewood.network.exception.ServerException
import uk.co.savills.stonewood.repository.entry.CommunalDataRepository
import uk.co.savills.stonewood.repository.property.PropertyRepository.Companion.EXT_BLOCK_PHOTOS_DIRECTORY
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.service.imageupload.ImageUploadWorker
import uk.co.savills.stonewood.util.Result
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.WifiLocker
import uk.co.savills.stonewood.util.getNonNullValue
import uk.co.savills.stonewood.util.photo.deletePhotosFolder
import java.io.File
import java.io.FileNotFoundException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.time.Duration
import java.time.Instant
import javax.net.ssl.SSLException

class DataTransferViewModel(application: Application) : BaseViewModel(application) {
    var hasAlterations = MutableLiveData(true)
    var alterations = MutableLiveData("")

    private lateinit var dataTransferRequest: DataTransferRequestModel

    private val _canTransferData = MutableLiveData<Boolean>()
    val canTransferData: LiveData<Boolean>
        get() = _canTransferData

    val isTransferringData
        get() = isBusy

    val dataTransferComplete = SingleLiveEvent<Nothing?>()

    val internetConnectionIssueDetected = SingleLiveEvent<Nothing?>()

    private val projectId: String
        get() = appState.currentProject.id

    private lateinit var surveySpecifications: DataTransferResponseModel

    private val wifiLocker by lazy { WifiLocker(application) }

    private lateinit var dataTransferStartTime: Instant

    private var faultyNetworkRetryCount = 0
    private var photoUploadRetryCount = 0

    init {
        hasAlterations.observeForever { evaluateAlterations() }
        alterations.observeForever { evaluateAlterations() }

        isBusy.observeForever { isBusy ->
            if (!isBusy) {
                evaluateAlterations()
            } else {
                _canTransferData.value = false
            }
        }
    }

    private fun evaluateAlterations() {
        _canTransferData.value = if (hasAlterations.getNonNullValue()) {
            alterations.getNonNullValue().isNotBlank()
        } else {
            true
        }
    }

    fun transferData() {
        _isBusy.postValue(true)
        dataTransferStartTime = Instant.now()

        viewModelScope.launch(Dispatchers.IO) {
            wifiLocker.holdLock()

            ImageUploadWorker.cancel(application)

            dataTransferRequest = getSurveyData()

            when (
                val result = apiService.sendAlterationEmail(
                    dataTransferRequest.alteration,
                    dataTransferRequest.propertyEntries
                )
            ) {
                is Result.Success -> downloadSurveySpecifications {
                    downloadCommunalImages {
                        downloadExtBlockPhotos(::uploadSurveyData)
                    }
                }
                is Result.Error -> handleApiCallError(result.exception, mapOf())
                null -> TODO()
            }
        }.invokeOnCompletion {
            wifiLocker.releaseLock()
            _isBusy.postValue(false)
        }
    }

    private suspend fun downloadSurveySpecifications(successCallback: suspend () -> Unit) {
        val info = mapOf("When" to "Specifications download")
        try {
            when (val result = apiService.getSurveySpecifications()) {
                is Result.Success -> {
                    surveySpecifications = result.data
                    successCallback.invoke()
                }

                is Result.Error -> {
                    handleApiCallError(result.exception, info)
                }

                null -> TODO()
            }
        } catch (e: Exception) {
            handleUnexpectedError(e, info)
        }
    }

    private suspend fun downloadCommunalImages(successCallback: suspend () -> Unit) {
        val info = mapOf("When" to "Communal image download")

        val rootDirectory = File(application.filesDir, PHOTO_DIRECTORY)
        val parentDirectory = File(
            rootDirectory,
            CommunalDataRepository.COMMUNAL_PHOTOS_DIRECTORY
        )
        val directory = File(parentDirectory, projectId)

        surveySpecifications.communalData.forEach { data ->
            val toBeRemoved = mutableListOf<String>()
            val toBeAdded = mutableListOf<String>()

            for (imagePath in data.imagePaths) {
                val request =
                    ImageRequestModel(data.surveyor, requireNotNull(data.syncId), imagePath)

                val file = File(directory, imagePath)
                if (file.exists()) {
                    toBeRemoved.add(imagePath)
                    toBeAdded.add(file.path)
                    continue
                }

                val result = appContainer.communalDataRepository.downloadCommunalImage(
                    request,
                    directory
                )

                when (result) {
                    is Result.Success -> {
                        toBeRemoved.add(imagePath)
                        toBeAdded.add(result.data)
                    }

                    is Result.Error -> {
                        if (result.exception !is FileNotFoundException) {
                            handleApiCallError(result.exception, info)
                            return
                        }
                    }

                    else -> {}
                }
            }

            data.imagePaths.removeAll(toBeRemoved)
            data.imagePaths.addAll(toBeAdded)
        }

        successCallback.invoke()
    }

    private suspend fun downloadExtBlockPhotos(successCallback: suspend () -> Unit) {
        val info = mapOf("When" to "Energy external image download")

        val rootDirectory = File(application.filesDir, PHOTO_DIRECTORY)
        val parentDirectory = File(
            rootDirectory,
            EXT_BLOCK_PHOTOS_DIRECTORY
        )
        val directory = File(parentDirectory, projectId)

        surveySpecifications.extBlockPhotos.forEach { data ->

            val toBeRemoved = mutableListOf<String>()
            val toBeAdded = mutableListOf<String>()

            val imagesToBeDownloaded = data.imagePaths.filter {
                val file = File(directory, it)
                val doesFileExist = file.exists()

                if (doesFileExist) {
                    toBeRemoved.add(it)
                    toBeAdded.add(file.path)
                }

                !file.exists()
            }

            val result = appContainer.externalPhotosRepository.downloadExtBlockPhotos(
                imagesToBeDownloaded,
                directory
            )

            when (result) {
                is Result.Success -> {
                    toBeRemoved.addAll(imagesToBeDownloaded)
                    toBeAdded.addAll(result.data)
                }

                is Result.Error -> {
                    handleApiCallError(result.exception, info)
                    return
                }

                else -> {

                }
            }

            data.imagePaths.removeAll(toBeRemoved)
            data.imagePaths.addAll(toBeAdded)
        }

        successCallback.invoke()
    }

    private suspend fun uploadSurveyData() {
        val images = appContainer.getAllImages(appState.currentProject.id)
        val history = appContainer.imageUploadHistoryRepository.get(projectId)

        uploadImages(images) {
            val externalImages = dataTransferRequest.propertyEntries.flatMap { property ->
                property.extBlockPhotos.map { File(it) }
            }.filter {
                it.exists() && !history.contains(it.path)
            }
            uploadExtBlockPhotos(externalImages) {
                uploadData(images, externalImages)
            }
        }
    }

    private suspend fun uploadData(images: List<File>, externalImages: List<File>) {
        try {
            when (val result = apiService.transferData(dataTransferRequest)) {
                is Result.Success -> {
                    clearSurveyData()
                    saveSurveySpecifications()
                    dataTransferComplete.call()
                    faultyNetworkRetryCount = 0
                }

                is Result.Error -> {
                    val info = mutableMapOf(
                        "When" to "Data upload",
                        "Images uploaded" to images.size.toString(),
                        "Energy external photos uploaded" to externalImages.size.toString()
                    )
                    handleApiCallError(result.exception, info)
                }

                else -> {}
            }
        } catch (e: Exception) {
            val info = mutableMapOf(
                "When" to "Database update",
                "Images uploaded" to images.size.toString(),
                "Energy external photos uploaded" to externalImages.size.toString()
            )
            handleUnexpectedError(e, info)
        }
    }

    private suspend fun uploadImages(
        images: List<File>,
        successCallback: suspend () -> Unit
    ) {
        var uploadedImageCount = 0

        val info = mutableMapOf(
            "When" to "Photos upload",
            "Total photos" to images.size.toString(),
        )

        if (images.any()) {
            images.chunked(IMAGE_UPLOAD_COUNT).forEach { chunk ->
                try {
                    when (val result = uploadImageChunk(chunk)) {
                        is Result.Success -> {
                            uploadedImageCount += chunk.size
                        }
                        is Result.Error -> {
                            info["Uploaded photo count"] = uploadedImageCount.toString()
                            handleApiCallError(result.exception, info)
                            return
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    info["Uploaded photo count"] = uploadedImageCount.toString()
                    handleUnexpectedError(e, info)
                    return
                }
            }
        }

        photoUploadRetryCount = 0
        successCallback.invoke()
    }

    private suspend fun uploadImageChunk(chunk: List<File>): Result<Unit>? {
        val result = apiService.uploadImages(appState.currentProject.id, chunk)

        when (result) {
            is Result.Success -> {
                chunk.forEach { it.delete() }
            }
            is Result.Error -> {
                if (result.exception is FileNotFoundException && photoUploadRetryCount < 3) {
                    photoUploadRetryCount++
                    return uploadImageChunk(chunk)
                }
            }

            else -> {}
        }

        photoUploadRetryCount = 0
        return result
    }

    private suspend fun uploadExtBlockPhotos(
        images: List<File>,
        successCallback: suspend () -> Unit
    ) {
        var uploadedImageCount = 0

        val info = mutableMapOf(
            "When" to "Energy External Photos upload",
            "Total photos" to images.size.toString(),
        )

        if (images.any()) {
            images.chunked(IMAGE_UPLOAD_COUNT).forEach { chunk ->
                try {
                    when (val result = uploadExtBlockImageChunk(chunk)) {
                        is Result.Success -> {
                            uploadedImageCount += chunk.size
                        }
                        is Result.Error -> {
                            info["Uploaded photo count"] = uploadedImageCount.toString()
                            handleApiCallError(result.exception, info)
                            return
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    info["Uploaded photo count"] = uploadedImageCount.toString()
                    handleUnexpectedError(e, info)
                    return
                }
            }
        }

        successCallback.invoke()
    }

    private suspend fun uploadExtBlockImageChunk(chunk: List<File>): Result<Unit>? {
        val result = apiService.uploadExtBlockImages(chunk)

        if (result is Result.Error) {
            if (result.exception is FileNotFoundException && photoUploadRetryCount < 3) {
                photoUploadRetryCount++
                return uploadImageChunk(chunk)
            }
        }

        photoUploadRetryCount = 0
        return result
    }

    private var properties = listOf<PropertyModel>()
    private val completedProperties = mutableListOf<PropertyModel>()

    private fun getSurveyData(): DataTransferRequestModel {
        return with(appContainer) {
            properties = propertyRepository.getProperties(projectId)

            val energySurveyEntries: MutableList<EnergySurveyElementEntryModel> = mutableListOf()
            val hhsrsSurveyEntries: MutableList<HHSRSSurveyElementEntryModel> = mutableListOf()
            val qualityStandardSurveyEntries: MutableList<QualityStandardSurveyElementEntryModel> =
                mutableListOf()
            val riskAssessmentSurveyEntries: MutableList<RiskAssessmentSurveyElementEntryModel> =
                mutableListOf()
            val stockStandardSurveySurveyEntries: MutableList<StockSurveyElementEntryModel> =
                mutableListOf()

            properties.forEach {
                if (it.status == PropertyStatus.SURVEYED) {
                    completedProperties.add(it)
                    energySurveyEntries.addAll(energySurveyElementEntryRepository.getEntries(it.UPRN))
                    hhsrsSurveyEntries.addAll(
                        hhsrsSurveyElementEntryRepository.getPropertyEntries(it.UPRN)
                    )
                    qualityStandardSurveyEntries.addAll(
                        qualityStandardSurveyElementEntryRepository.getPropertyEntries(it.UPRN)
                    )
                    riskAssessmentSurveyEntries.addAll(
                        riskAssessmentSurveyElementEntryRepository.getPropertyEntries(it.UPRN)
                    )
                    stockStandardSurveySurveyEntries.addAll(
                        stockStandardSurveyElementEntryRepository.getEntries(it.UPRN, projectId)
                    )
                }
            }

            val completedPropertyUPRNs = completedProperties.map { it.UPRN }
            communalDataRepository.updatePhotoStorage(projectId, completedPropertyUPRNs)

            DataTransferRequestModel(
                completedProperties,
                energySurveyEntries,
                hhsrsSurveyEntries,
                qualityStandardSurveyEntries,
                riskAssessmentSurveyEntries,
                stockStandardSurveySurveyEntries,
                noAccessEntryRepository.getEntries(projectId),
                communalDataRepository.getNewEntries(projectId, completedPropertyUPRNs),
                externalPhotosRepository.getNewEntries(
                    projectId,
                    properties = completedPropertyUPRNs
                ),
                AlterationModel(!hasAlterations.getNonNullValue(), alterations.getNonNullValue())
            ).apply {
                syncStartTime = Instant.now()
            }
        }
    }

    private fun clearSurveyData() {
        with(appContainer) {
            val redundantProperties = properties.filterNot { property ->
                surveySpecifications.properties.any { property.UPRN == it.UPRN }
            }

            val propertiesToBeCleared = (completedProperties + redundantProperties).distinct()

            propertyRepository.clearProperties(propertiesToBeCleared.map { it.id })

            qualityStandardSurveyElementRepository.clearElements()
            riskAssessmentSurveyElementRepository.clearElements()
            hhsrsSurveyElementRepository.clearElements()
            energySurveyElementRepository.clearElements()
            stockSurveyElementRepository.clearElements()
            validationElementRepository.clearElements(projectId)
            photoNoAccessReasonRepository.clearReasons()
            noAccessReasonRepository.clearReasons()
            communalDataRepository.clearOldEntries(projectId)
            hhsrsLocationRepository.clearLocations()
            ageBandRepository.clearBands()
            renewalBandRepository.clearBands()

            val propertyUPRNs = propertiesToBeCleared.map { it.UPRN }

            energySurveyElementEntryRepository.clearEntries(propertyUPRNs)
            hhsrsSurveyElementEntryRepository.clearEntries(propertyUPRNs)
            qualityStandardSurveyElementEntryRepository.clearEntries(propertyUPRNs)
            riskAssessmentSurveyElementEntryRepository.clearEntries(propertyUPRNs)
            stockStandardSurveyElementEntryRepository.clearEntries(propertyUPRNs)
            noAccessEntryRepository.clearEntries(propertyUPRNs)
            communalDataRepository.clearOldEntries(projectId)
            communalDataRepository.updateNewEntries(projectId, propertyUPRNs)
            externalPhotosRepository.clearOldEntries(projectId)
            externalPhotosRepository.updateNewEntries(projectId, propertyUPRNs)

            propertyStatsRepository.clear(projectId)

            for (UPRN in propertyUPRNs) {
                deletePhotosFolder(
                    application.applicationContext,
                    "${projectId}_$UPRN"
                )
            }
        }
    }

    private fun saveSurveySpecifications() {
        if (::surveySpecifications.isInitialized) {
            with(appContainer) {
                appState.currentProject = surveySpecifications.project

                val properties = surveySpecifications.properties.filterNot { property ->
                    completedProperties.any { it.id == property.id }
                }
                propertyRepository.insertProperties(properties)

                noAccessReasonRepository.insertReasons(surveySpecifications.noAccessReasons)
                hhsrsLocationRepository.insertLocations(surveySpecifications.hhsrsLocations.sortedBy { it.id })
                ageBandRepository.insertBands(surveySpecifications.ageBands)
                renewalBandRepository.insertBands(surveySpecifications.renewalBands)
                qualityStandardSurveyElementRepository.insertElements(surveySpecifications.qualityStandardSurveyElements)
                riskAssessmentSurveyElementRepository.insertElements(surveySpecifications.riskAssessmentSurveyElements)
                hhsrsSurveyElementRepository.insertElements(surveySpecifications.hhsrsSurveyElements)
                energySurveyElementRepository.insertElements(surveySpecifications.energySurveyElements)
                stockSurveyElementRepository.insertElements(surveySpecifications.stockSurveyElements)
                validationElementRepository.insertElements(
                    projectId,
                    surveySpecifications.validationElements
                )
                photoNoAccessReasonRepository.insertReasons(surveySpecifications.photoNoAccessReasons)
                communalDataRepository.insertEntries(projectId, surveySpecifications.communalData)
                externalPhotosRepository.insertEntries(
                    projectId,
                    surveySpecifications.extBlockPhotos
                )

                propertyStatsRepository.insertStats(
                    projectId,
                    surveySpecifications.projectStatistics
                )
                propertyStatsRepository.insertStats(
                    projectId,
                    completedProperties.map { it.toStatsModel() }
                )
            }
        }
    }

    private fun handleApiCallError(error: Exception, info: Map<String, String>) {
        val isConnectionIssue = error is SocketException ||
            error is SSLException ||
            error is SocketTimeoutException

        if (!isConnectionIssue) faultyNetworkRetryCount = 0

        when {
            error is ServerException -> serverConnectionError.postValue(error.message)

            error is NetworkUnavailableException -> noConnectionError.call()

            isConnectionIssue -> {
                if (faultyNetworkRetryCount < 3) {
                    faultyNetworkRetryCount++
                    transferData()
                } else {
                    faultyNetworkRetryCount = 0
                    reportError(error, info)
                    internetConnectionIssueDetected.call()
                }
            }

            else -> handleUnexpectedError(error, info)
        }
    }

    private fun handleUnexpectedError(error: Exception, info: Map<String, String>) {
        reportError(error, info)
        unexpectedError.postValue(error.message)
    }

    private fun reportError(error: Exception, info: Map<String, String>) {
        val duration = Duration.between(dataTransferStartTime, Instant.now())

        val properties = mapOf(
            "Surveyor" to appState.profile?.fullName.orEmpty(),
            "Project" to appState.currentProject.name,
            "Duration" to DateUtils.formatElapsedTime(duration.seconds)
        ) + info
        if (BuildConfig.DEBUG) {
            Log.d("Data transfer -->", properties.toString())
        } else {
            appContainer.errorReportingService.reportError(error, properties)
        }
    }

    companion object {
        private const val IMAGE_UPLOAD_COUNT = 10
    }
}
