@file:Suppress("InvalidClassName")

package uk.co.savills.stonewood.screen.survey.survey.hhsrs

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.savills.stonewood.model.survey.HHSRSElementRating
import uk.co.savills.stonewood.model.survey.HHSRSSevereIssueModel
import uk.co.savills.stonewood.model.survey.PropertyLocationType
import uk.co.savills.stonewood.model.survey.element.HHSRSSurveyElementModel
import uk.co.savills.stonewood.model.survey.entry.HHSRSSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyViewModelBase
import uk.co.savills.stonewood.service.imageupload.ImageUploadWorker
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.getNonNullValue
import uk.co.savills.stonewood.util.photo.deletePhotoFile
import uk.co.savills.stonewood.util.photo.getFileName
import java.lang.Integer.max

class HHSRSSurveyViewModel(
    application: Application,
    locationTracker: LocationTracker
) : SurveyViewModelBase(application, locationTracker) {

    private val _elements = MutableLiveData<List<HHSRSSurveyElementModel>>()
    val elements: LiveData<List<HHSRSSurveyElementModel>>
        get() = _elements

    val ratings = HHSRSElementRating.values().toList()

    private val _isExtraInfoRequired = MutableLiveData<Boolean>()
    val isExtraInfoRequired: LiveData<Boolean>
        get() = _isExtraInfoRequired

    private lateinit var ratingDescriptionUpdateJob: Job
    val ratingDescription = MutableLiveData<String>()

    private val _internalLocations = MutableLiveData<List<String>>()
    val internalLocations = _internalLocations

    private val _isOtherInternalLocationInfoRequired = MutableLiveData(false)
    val isOtherInternalLocationInfoRequired: LiveData<Boolean>
        get() = _isOtherInternalLocationInfoRequired

    val otherInternalLocationInfo = MutableLiveData<String>()

    private lateinit var otherInternalLocationUpdateJob: Job

    private val _externalLocations = MutableLiveData<List<String>>()
    val externalLocations = _externalLocations

    private val _isOtherExternalLocationInfoRequired = MutableLiveData(false)
    val isOtherExternalLocationInfoRequired: LiveData<Boolean>
        get() = _isOtherExternalLocationInfoRequired

    val otherExternalLocationInfo = MutableLiveData<String>()

    private lateinit var otherExternalLocationUpdateJob: Job

    private val _selectedElement = MutableLiveData<HHSRSSurveyElementModel>()
    val selectedElement: LiveData<HHSRSSurveyElementModel>
        get() = _selectedElement

    val photoFolderName: String
        get() = "${appState.currentProject.id}_${appState.currentProperty.UPRN}"

    val photoFileName: String
        get() {
            val surveyor = requireNotNull(appState.profile).userName
            val element = selectedElement.getNonNullValue()
            return getFileName(
                surveyor,
                property.UPRN,
                element.entry.imagePaths.size + 1,
                "HHSRS_" + element.title
            )
        }

    private val _remainingPhotoCount = MutableLiveData(MIN_PHOTOS_REQUIRED)
    val remainingPhotoCount: LiveData<Int>
        get() = _remainingPhotoCount

    val elementsUpdated = SingleLiveEvent<Nothing?>()
    val elementAutoSelected = SingleLiveEvent<Int>()
    val ratingChangedToTypical = SingleLiveEvent<Nothing?>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getPropertyLocations()
            getElements()
        }

        selectedElement.observeForever { evaluateRemainingPhotoCount() }
        elementsUpdated.observeForever { evaluateRemainingPhotoCount() }

        ratingDescription.observeForever { description ->
            viewModelScope.launch(Dispatchers.Main) {
                if (::ratingDescriptionUpdateJob.isInitialized) ratingDescriptionUpdateJob.cancelAndJoin()

                ratingDescriptionUpdateJob = launch(Dispatchers.Main) {
                    updateSelectedElement { element ->
                        element.entry.ratingDescription = description
                    }
                }
            }
        }

        otherInternalLocationInfo.observeForever { info ->
            viewModelScope.launch(Dispatchers.Main) {
                if (::otherInternalLocationUpdateJob.isInitialized) {
                    otherInternalLocationUpdateJob.cancelAndJoin()
                }

                otherInternalLocationUpdateJob = launch(Dispatchers.Main) {
                    updateSelectedElement { element ->
                        element.entry.internalLocations.find { it.startsWith(OTHER_LOCATION_PREFIX) }
                            ?.let {
                                element.entry.internalLocations =
                                    element.entry.internalLocations - it
                            }

                        if (info.isNotBlank()) {
                            element.entry.internalLocations =
                                element.entry.internalLocations + (OTHER_LOCATION_PREFIX + info)
                        }
                    }
                }
            }
        }

        otherExternalLocationInfo.observeForever { info ->
            viewModelScope.launch(Dispatchers.Main) {
                if (::otherExternalLocationUpdateJob.isInitialized) {
                    otherExternalLocationUpdateJob.cancelAndJoin()
                }

                otherExternalLocationUpdateJob = launch(Dispatchers.Main) {
                    updateSelectedElement { element ->
                        element.entry.externalLocations.find { it.startsWith(OTHER_LOCATION_PREFIX) }
                            ?.let {
                                element.entry.externalLocations =
                                    element.entry.externalLocations - it
                            }

                        if (info.isNotBlank()) {
                            element.entry.externalLocations =
                                element.entry.externalLocations + (OTHER_LOCATION_PREFIX + info)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getElements() {
        val elements =
            appContainer.hhsrsSurveyElementRepository.getElements().filter { !it.exclude }

        withContext(Dispatchers.Main) {
            setSelectedElement(elements.first())
        }

        _elements.postValue(elements)
    }

    private suspend fun getPropertyLocations() {
        val hhsrsLocations = appContainer.hhsrsLocationRepository.getLocations()
        val internalLocations = hhsrsLocations.filter {
            it.type == PropertyLocationType.INTERNAL
        }.map {
            it.name
        }

        val externalLocations = hhsrsLocations.filter {
            it.type == PropertyLocationType.EXTERNAL
        }.map {
            it.name
        }

        withContext(Dispatchers.Main) {
            _internalLocations.value = internalLocations
            _externalLocations.value = externalLocations
        }
    }

    private fun setSelectedElement(element: HHSRSSurveyElementModel) {
        element.isSelected = true
        _selectedElement.value = element

        _isExtraInfoRequired.value = element.isExtraInformationRequired

        ratingDescription.value = element.entry.ratingDescription

        val otherInternalLocation =
            element.entry.internalLocations.find { it.startsWith(OTHER_LOCATION_PREFIX) }
        _isOtherInternalLocationInfoRequired.value = otherInternalLocation != null
        otherInternalLocationInfo.value =
            otherInternalLocation?.removePrefix(OTHER_LOCATION_PREFIX).orEmpty()

        val otherExternalLocation =
            element.entry.externalLocations.find { it.startsWith(OTHER_LOCATION_PREFIX) }
        _isOtherExternalLocationInfoRequired.value = otherExternalLocation != null
        otherExternalLocationInfo.value =
            otherExternalLocation?.removePrefix(OTHER_LOCATION_PREFIX).orEmpty()
    }

    fun onElementSelected(position: Int) {
        _elements.value = _elements.getNonNullValue().mapIndexed { index, element ->
            val isSelected = index == position

            element.copy(isSelected = isSelected).also {
                if (isSelected) setSelectedElement(it)
            }
        }
    }

    fun onRatingSelected(rating: HHSRSElementRating) {
        val previousRating = selectedElement.getNonNullValue().entry.rating

        if (
            previousRating != null &&
            previousRating != HHSRSElementRating.TYPICAL &&
            rating == HHSRSElementRating.TYPICAL
        ) {
            ratingChangedToTypical.call()
            return
        }

        updateSelectedElement {
            it.entry.rating = rating

            _isExtraInfoRequired.value = it.isExtraInformationRequired
        }

        if (rating == HHSRSElementRating.TYPICAL) autoSelectNextElement()
    }

    fun changeRatingToTypical() {
        updateSelectedElement {
            it.entry.changedToTypicalFrom = it.entry.rating
            it.entry.rating = HHSRSElementRating.TYPICAL

            _isExtraInfoRequired.value = it.isExtraInformationRequired
        }

        autoSelectNextElement()
    }

    fun onImageAdded(filePath: String) {
        updateSelectedElement {
            it.entry.imagePaths.add(0, filePath)
        }
    }

    fun onImageRemoved(filePath: String) {
        updateSelectedElement {
            it.entry.imagePaths.remove(filePath)
            deletePhotoFile(filePath)
        }
    }

    fun onInternalLocationSelected(location: String) {
        updateSelectedElement { element ->
            element.entry.internalLocations =
                (element.entry.internalLocations + location).distinct()
        }
    }

    fun onInternalLocationDeselected(location: String) {
        updateSelectedElement { element ->
            element.entry.internalLocations =
                (element.entry.internalLocations - location).distinct()
        }
    }

    fun onExternalLocationSelected(location: String) {
        updateSelectedElement { element ->
            element.entry.externalLocations =
                (element.entry.externalLocations + location).distinct()
        }
    }

    fun onExternalLocationDeselected(location: String) {
        updateSelectedElement { element ->
            element.entry.externalLocations =
                (element.entry.externalLocations - location).distinct()
        }
    }

    fun onOtherInternalLocationSelected() {
        _isOtherInternalLocationInfoRequired.value = true
        updateSelectedElement()
    }

    fun onOtherInternalLocationDeselected() {
        _isOtherInternalLocationInfoRequired.value = false
        otherInternalLocationInfo.value = ""

        updateSelectedElement { element ->
            element.entry.internalLocations.find { it.startsWith(OTHER_LOCATION_PREFIX) }?.let {
                element.entry.internalLocations = element.entry.internalLocations - it
            }
        }
    }

    fun onOtherExternalLocationSelected() {
        _isOtherExternalLocationInfoRequired.value = true
        updateSelectedElement()
    }

    fun onOtherExternalLocationDeselected() {
        _isOtherExternalLocationInfoRequired.value = false
        otherExternalLocationInfo.value = ""

        updateSelectedElement { element ->
            element.entry.externalLocations.find { it.startsWith(OTHER_LOCATION_PREFIX) }?.let {
                element.entry.externalLocations = element.entry.externalLocations - it
            }
        }
    }

    private fun autoSelectNextElement() {
        val elements = elements.getNonNullValue()
        elements.forEachIndexed { index, element ->
            if (element.id == selectedElement.getNonNullValue().id && index < elements.size - 1) {
                elementAutoSelected.postValue(index + 1)
            }
        }
    }

    private fun updateSelectedElement(update: ((HHSRSSurveyElementModel) -> Unit)? = null) {
        _elements.value?.forEach {
            if (it.id == selectedElement.getNonNullValue().id) {
                update?.invoke(it)
                it.entry.isComplete = evaluateSelectedElementCompletionStatus()
                elementsUpdated.call()
            }
        }
    }

    private fun evaluateSelectedElementCompletionStatus(): Boolean {
        val selectedElement = selectedElement.getNonNullValue()
        selectedElement.entry.isComplete = isElementComplete(selectedElement)

        viewModelScope.launch(Dispatchers.IO) {
            with(selectedElement) {
                if (entry.rating != null) {
                    entry.details = getSurveyDetails(entry.details?.entryInstant)
                    appContainer.hhsrsSurveyElementEntryRepository.insertEntry(entry)

                    if (entry.rating == HHSRSElementRating.SEVERE && isComplete) {
                        processSevereIssue(entry)
                    }

                    if (entry.imagePaths.any()) ImageUploadWorker.beginWork(application)
                } else {
                    appContainer.hhsrsSurveyElementEntryRepository.clearEntry(
                        id,
                        property.UPRN
                    )
                }
            }
        }

        updateSurveyCompletionStatus(
            SurveyType.HHSRS,
            _elements.getNonNullValue().all { it.isComplete }
        )

        return selectedElement.isComplete
    }

    private fun processSevereIssue(entry: HHSRSSurveyElementEntryModel) {
        val existing = issueService.getIssue(appState.currentProject.id, property.id, entry.id)
        val issue = HHSRSSevereIssueModel.from(
            entry,
            appState.profile?.id ?: return,
            appState.currentProject.id,
            property.id
        )

        if (issue != existing) {
            issueService.saveIssue(issue)
        }
    }

    private fun isElementComplete(element: HHSRSSurveyElementModel): Boolean {
        with(element.entry) {
            if (rating != null) {
                if (element.isExtraInformationRequired) {
                    if (isOtherInternalLocationInfoRequired.getNonNullValue()) {
                        if (!internalLocations.any { it.startsWith(OTHER_LOCATION_PREFIX) }) {
                            return false
                        }
                    }

                    if (isOtherExternalLocationInfoRequired.getNonNullValue()) {
                        if (!externalLocations.any { it.startsWith(OTHER_LOCATION_PREFIX) }) {
                            return false
                        }
                    }

                    return ratingDescription.isNotBlank() &&
                        imagePaths.size >= MIN_PHOTOS_REQUIRED &&
                        (internalLocations.any() || externalLocations.any())
                } else {
                    return true
                }
            }

            return false
        }
    }

    private fun evaluateRemainingPhotoCount() {
        val imageCount = selectedElement.getNonNullValue().entry.imagePaths.size
        _remainingPhotoCount.value = max(0, MIN_PHOTOS_REQUIRED - imageCount)
    }

    companion object {
        const val OTHER_LOCATION_PREFIX = "Other: "
        private const val MIN_PHOTOS_REQUIRED = 1
    }
}
