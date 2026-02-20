package uk.co.savills.stonewood.screen.survey.survey.riskassessment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer
import uk.co.savills.stonewood.model.survey.element.RiskAssessmentSurveyElementModel
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel
import uk.co.savills.stonewood.model.survey.noaccess.NoAccessEntryModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.repository.entry.ExtBlockPhotosRepository
import uk.co.savills.stonewood.repository.property.PropertyRepository.Companion.EXT_BLOCK_PHOTOS_DIRECTORY
import uk.co.savills.stonewood.repository.property.PropertyRepository.Companion.EXT_BLOCK_PHOTO_TAG
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyViewModelBase
import uk.co.savills.stonewood.service.imageupload.ImageUploadWorker
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.getNonNullValue
import uk.co.savills.stonewood.util.photo.getFileName
import java.io.File
import java.time.Instant

class RiskAssessmentSurveyViewModel(
    application: Application,
    locationTracker: LocationTracker
) : SurveyViewModelBase(application, locationTracker) {

    private val _elements = MutableLiveData<List<RiskAssessmentSurveyElementModel>>()
    val elements: LiveData<List<RiskAssessmentSurveyElementModel>>
        get() = _elements

    val allQuestionsAnswered = SingleLiveEvent<Pair<Boolean, Boolean>>()

    val frontDoorPhotoSuccessfullyAdded = SingleLiveEvent<Nothing?>()

    val frontDoorPhotoFolderName: String
        get() = "${appState.currentProject.id}_${property.UPRN}"

    val frontDoorPhotoFileName: String =
        getFileName(appState.profile?.userName.orEmpty(), property.UPRN, null, "FrontDoor")

    val extPhotoFileName
        get() = getFileName(
            appState.profile?.userName.orEmpty(),
            property.UPRN,
            property.extBlockPhotos.size + 1,
            EXT_BLOCK_PHOTO_TAG
        )

    val extPhotoFolderName: String
        get() = "$EXT_BLOCK_PHOTOS_DIRECTORY${File.pathSeparator}${appState.currentProject.id}"

    val extPhotos
        get() = property.extBlockPhotos

    val areExtPhotosRequired: Boolean
        get() = property.hasExternalPhoto

    val requiredExtPhotoCount: Int
        get() = project.numberOfSharedExternalPhotos

    private val externalPhotoRepository: ExtBlockPhotosRepository
        get() = appContainer.externalPhotosRepository

    val noAccessPhotoFileName: String
        get() {
            val surveyor = requireNotNull(appState.profile).userName
            return getFileName(surveyor, property.UPRN, elementName = "_NoAccess_COVIDFailure")
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val elements = appContainer.riskAssessmentSurveyElementRepository.getElements()
                .filter { !it.exclude }
            _elements.postValue(elements)
        }
    }

    fun onQuestionAnswered(
        element: RiskAssessmentSurveyElementModel,
        answer: CloseEndedQuestionAnswer
    ) {
        updateElements(element, answer)

        val elements = _elements.getNonNullValue()

        viewModelScope.launch(Dispatchers.IO) {
            saveEntry(element, answer)
        }.invokeOnCompletion {
            updateSurveyCompletionStatus(
                SurveyType.RISK_ASSESSMENT,
                elements.all { it.isAnswerValid }
            )
        }

        if (elements.all { it.answer != CloseEndedQuestionAnswer.UNANSWERED }) {
            val isFrontDoorPhotoRequired =
                property.isFrontDoorPhotoRequired && property.frontDoorPhoto.isBlank()

            allQuestionsAnswered.value =
                elements.all { it.isAnswerValid } to isFrontDoorPhotoRequired
        }
    }

    fun onFrontDoorPhotoTaken(filePath: String) {
        property.frontDoorPhoto = filePath
        property.updatedAt = Instant.now()

        viewModelScope.launch(Dispatchers.IO) {
            appContainer.propertyRepository.updateProperty(property)
            appContainer.dataBackUpService.backupData(appState.currentProject.id)
            ImageUploadWorker.beginWork(application)
        }.invokeOnCompletion {
            frontDoorPhotoSuccessfullyAdded.call()
            surveyUpdated.call()
        }
    }

    fun onNoAccessPhotoTaken(filePath: String) {
        val noAccessEntry = NoAccessEntryModel(
            property.UPRN,
            RiskAssessmentSurveyElementModel.NO_ACCESS_REASON,
            "",
            listOf(filePath),
            locationTracker.getCurrentLocation(),
            Instant.now()
        )

        viewModelScope.launch(Dispatchers.IO) {
            appContainer.noAccessEntryRepository.insertEntry(
                noAccessEntry,
                property.id
            )
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
                appState.currentProject.id,
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
                    appState.currentProject.id,
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

    private fun updateElements(
        element: RiskAssessmentSurveyElementModel,
        answer: CloseEndedQuestionAnswer
    ) {
        _elements.value = _elements.getNonNullValue().map {
            if (it.id == element.id) {
                it.copy().apply { it.entry.answer = answer }
            } else {
                it
            }
        }
    }

    private fun saveEntry(
        element: RiskAssessmentSurveyElementModel,
        answer: CloseEndedQuestionAnswer
    ) {
        appContainer.riskAssessmentSurveyElementEntryRepository.insertEntry(
            element.entry.apply {
                this.answer = answer
                this.details = getSurveyDetails(details?.entryInstant)
            }
        )
    }
}
