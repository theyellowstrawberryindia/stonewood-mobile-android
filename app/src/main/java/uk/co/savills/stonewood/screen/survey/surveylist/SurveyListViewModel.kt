package uk.co.savills.stonewood.screen.survey.surveylist

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.element.validation.ValidationCategory
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.model.survey.property.PropertyStatus
import uk.co.savills.stonewood.repository.entry.ExtBlockPhotosRepository
import uk.co.savills.stonewood.repository.property.PropertyRepository
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.service.imageupload.ImageUploadWorker
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.getNonNullValue
import uk.co.savills.stonewood.util.photo.getFileName
import java.io.File
import java.time.Instant

class SurveyListViewModel(application: Application) : BaseViewModel(application) {

    val property: PropertyModel
        get() = appContainer.appState.currentProperty

    private val _surveys = MutableLiveData<List<SurveyModel>>()
    val surveys: LiveData<List<SurveyModel>>
        get() = _surveys

    val projectTitle = project.name

    private val _canMoveBack = MutableLiveData<Boolean>()
    val canMoveBack: LiveData<Boolean>
        get() = _canMoveBack

    private val _canTakeFrontDoorPhoto = MutableLiveData(property.frontDoorPhoto.isEmpty())
    val canTakeFrontDoorPhoto: LiveData<Boolean>
        get() = _canTakeFrontDoorPhoto

    private val _frontDoorPhoto = MutableLiveData(property.frontDoorPhoto)
    val frontDoorPhoto: LiveData<String>
        get() = _frontDoorPhoto

    val photoFolderName: String
        get() = "${project.id}_${property.UPRN}"

    val photoFileName: String
        get() {
            val surveyor = requireNotNull(appState.profile).userName
            return getFileName(surveyor, property.UPRN, null, "FrontDoor")
        }

    val takePhoto = SingleLiveEvent<Nothing?>()

    val isFrontDoorPhotoRequired
        get() = property.isFrontDoorPhotoRequired

    val extPhotoFileName
        get() = getFileName(
            requireNotNull(appState.profile).userName,
            property.UPRN,
            property.extBlockPhotos.size + 1,
            "ExternalBlock"
        )

    val extPhotoFolderName: String
        get() = "${PropertyRepository.EXT_BLOCK_PHOTOS_DIRECTORY}${File.pathSeparator}${project.id}"

    val extPhotos
        get() = property.extBlockPhotos

    val requiredExtPhotoCount: Int
        get() = project.numberOfSharedExternalPhotos

    private val externalPhotoRepository: ExtBlockPhotosRepository
        get() = appContainer.externalPhotosRepository

    private val project
        get() = appState.currentProject

    fun getSurveys() {
        viewModelScope.launch(Dispatchers.IO) {
            var surveys = project.getSurveys(property.surveyType)
            val validations =
                appContainer.validationElementRepository.getElements(project.id, appState.currentProperty.UPRN)

            val countMap = validations.groupBy { it.category }

            val isStockSurveyPresent = surveys.any { it.type == SurveyType.STOCK }
            val isEnergySurveyPresent = surveys.any { it.type == SurveyType.ENERGY }

            val needsValidation = (isStockSurveyPresent && isEnergySurveyPresent && validations.isNotEmpty()) ||
                (isStockSurveyPresent && countMap[ValidationCategory.S_LOG]?.isNotEmpty() == true) ||
                isEnergySurveyPresent && countMap[ValidationCategory.E_LOG]?.isNotEmpty() == true

            surveys =
                if (needsValidation) {
                    surveys + SurveyModel(SurveyType.VALIDATION, null, false)
                } else {
                    surveys
                }

            surveys = surveys.onEach {
                it.isComplete =
                    when (it.type) {
                        SurveyType.RISK_ASSESSMENT -> property.surveyStatus.isRiskAssessmentSurveyComplete
                        SurveyType.QUALITY_STANDARD -> property.surveyStatus.isQualityStandardSurveyComplete
                        SurveyType.STOCK -> property.surveyStatus.isStockSurveyComplete
                        SurveyType.ENERGY -> property.surveyStatus.isEnergySurveyComplete
                        SurveyType.HHSRS -> property.surveyStatus.isHHSRSSurveyComplete
                        SurveyType.VALIDATION -> property.surveyStatus.isValidationComplete
                    }
            }.also {
                appState.surveys = it
            }

            _surveys.postValue(surveys)
            _canMoveBack.postValue(canMoveBack(surveys))
        }
    }

    fun isSurveyEnabled(survey: SurveyModel) =
        property.isEnabled(project, survey, surveys.getNonNullValue())

    fun onSurveySelected(type: SurveyType) {
        navigateTo(SurveyListFragmentDirections.moveToSurveyTabScreen(type))
    }

    fun onContactDetail() {
        navigateTo(SurveyListFragmentDirections.moveToContactDetailScreen())
    }

    fun takeFrontDoorPhoto() = takePhoto.call()

    fun onFrontDoorPhotoTaken(filePath: String) {
        property.frontDoorPhoto = filePath
        property.updatedAt = Instant.now()

        viewModelScope.launch(Dispatchers.IO) {
            appContainer.propertyRepository.updateProperty(property)
            ImageUploadWorker.beginWork(application)
            appContainer.dataBackUpService.backupData(appState.currentProject.id)
        }.invokeOnCompletion {
            _canTakeFrontDoorPhoto.postValue(false)
            _frontDoorPhoto.postValue(filePath)
        }
    }

    fun onRemoveFrontDoorPhoto() {
        property.frontDoorPhoto = ""
        property.updatedAt = Instant.now()

        viewModelScope.launch(Dispatchers.IO) {
            appContainer.propertyRepository.updateProperty(property)
            appContainer.dataBackUpService.backupData(appState.currentProject.id)
        }.invokeOnCompletion {
            _canTakeFrontDoorPhoto.postValue(true)
        }
    }

    fun addExtBlockPhoto(filePath: String) {
        if (property.extPhotosClonedFrom.isNotEmpty()) {
            property.extPhotosClonedFrom = ""
            property.extBlockPhotos.clear()
        }

        property.extBlockPhotos.add(filePath)

        viewModelScope.launch(Dispatchers.IO) {
            externalPhotoRepository.updateEntry(
                project.id,
                property,
                appState.profile?.fullName.orEmpty(),
                property.extBlockPhotos
            )
            appContainer.propertyRepository.updateProperty(property)
            appContainer.dataBackUpService.backupData(appState.currentProject.id)
        }
    }

    fun removeExtBlockPhoto(filePath: String) {
        property.extBlockPhotos.remove(filePath)
        val externalPhotos = property.extBlockPhotos

        viewModelScope.launch(Dispatchers.IO) {
            if (externalPhotos.isEmpty()) {
                externalPhotoRepository.clearEntry(project.id, property.UPRN)
            } else {
                externalPhotoRepository.updateEntry(
                    project.id,
                    property,
                    appState.profile?.fullName.orEmpty(),
                    externalPhotos
                )
            }

            appContainer.propertyRepository.updateProperty(property)
            appContainer.dataBackUpService.backupData(appState.currentProject.id)
        }
    }

    fun onExistingPhotosSelected(extBlockPhoto: ExtBlockPhotoModel) {
        if (
            property.extPhotosClonedFrom == extBlockPhoto.propertyUPRN ||
            (property.extPhotosClonedFrom.isEmpty() && extBlockPhoto.propertyUPRN == property.UPRN)
        ) return

        with(property.extBlockPhotos) {
            forEach {
                val file = File(it)
                if (file.exists()) file.delete()
            }

            clear()
            addAll(extBlockPhoto.imagePaths)
        }

        val externalPropertyURRN = extBlockPhoto.propertyUPRN
        property.extPhotosClonedFrom = if (externalPropertyURRN == property.UPRN) "" else externalPropertyURRN
    }

    @WorkerThread
    private fun canMoveBack(surveys: List<SurveyModel>): Boolean {
        if (property.status == PropertyStatus.SURVEYED) return true

        for (survey in surveys) {
            when (survey.type) {
                SurveyType.RISK_ASSESSMENT -> {
                    val entries =
                        appContainer.riskAssessmentSurveyElementEntryRepository.getPropertyEntries(property.UPRN)

                    if (entries.any()) {
                        val riskAssessmentElements =
                            appContainer.riskAssessmentSurveyElementRepository.getElements()

                        return if (riskAssessmentElements.size == entries.size) {
                            !property.surveyStatus.isRiskAssessmentSurveyComplete
                        } else {
                            false
                        }
                    }
                }

                SurveyType.QUALITY_STANDARD -> {
                    val entries =
                        appContainer.qualityStandardSurveyElementEntryRepository.getPropertyEntries(
                            property.UPRN
                        )

                    if (entries.any()) {
                        return false
                    }
                }

                SurveyType.HHSRS -> {
                    val entries =
                        appContainer.hhsrsSurveyElementEntryRepository.getPropertyEntries(property.UPRN)

                    if (entries.any()) {
                        return false
                    }
                }

                SurveyType.ENERGY -> {
                    val entries =
                        appContainer.energySurveyElementEntryRepository.getEntries(property.UPRN)

                    if (entries.any()) {
                        return false
                    }
                }

                SurveyType.STOCK -> {
                    val entries =
                        appContainer.stockStandardSurveyElementEntryRepository.getEntries(
                            property.UPRN
                        )

                    if (entries.any()) {
                        return false
                    }
                }

                else -> {}
            }
        }

        return true
    }
}
