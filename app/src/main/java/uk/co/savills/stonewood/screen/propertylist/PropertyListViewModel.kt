package uk.co.savills.stonewood.screen.propertylist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import androidx.work.WorkInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.model.survey.property.PropertySurveyType
import uk.co.savills.stonewood.repository.property.PropertyRepository.Companion.EXT_BLOCK_PHOTOS_DIRECTORY
import uk.co.savills.stonewood.repository.property.PropertyRepository.Companion.EXT_BLOCK_PHOTO_TAG
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.service.PropertyDataUpdater
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.navigation.makePhoneCall
import uk.co.savills.stonewood.util.navigation.searchAddressOnMap
import uk.co.savills.stonewood.util.photo.getFileName
import java.io.File
import java.time.Instant

class PropertyListViewModel(application: Application) : BaseViewModel(application) {
    private val propertyRepository
        get() = appContainer.propertyRepository

    private val propertyPagingConfig = PagingConfig(
        PROPERTY_PAGE_SIZE,
        PROPERTY_PREFETCH_DISTANCE,
        initialLoadSize = PROPERTY_PAGE_SIZE,
    )

    lateinit var propertyPagingData: LiveData<PagingData<PropertyModel>>
        private set

    val propertyPagingDataChanged = SingleLiveEvent<Nothing?>()

    private val _propertyCount = MutableLiveData<Int>()
    val propertyCount: LiveData<Int>
        get() = _propertyCount

    var searchText = MutableLiveData<String>()
    lateinit var searchJob: Job

    private lateinit var selectedProperty: PropertyModel
    val iePropertySelected = SingleLiveEvent<Nothing?>()

    val photoFolderName: String
        get() = "${EXT_BLOCK_PHOTOS_DIRECTORY}${File.pathSeparator}${appState.currentProject.id}"

    private val onSearchTextChangeObserver = Observer { text: String? ->
        viewModelScope.launch(Dispatchers.IO) {
            if (::searchJob.isInitialized) searchJob.cancelAndJoin()

            searchJob = launch(Dispatchers.IO) {
                val propertyPagingSource = if (text.isNullOrBlank()) {
                    _propertyCount.postValue(propertyRepository.getPropertyCount())
                    propertyRepository.getPropertyPagingSource()
                } else {
                    _propertyCount.postValue(propertyRepository.getFilteredPropertyCount(text))
                    propertyRepository.getSearchPropertyPagingSource(text)
                }

                if (::searchJob.isInitialized && searchJob.isActive) {
                    val pager = Pager(propertyPagingConfig) {
                        propertyPagingSource
                    }

                    propertyPagingData = pager
                        .liveData
                        .cachedIn(viewModelScope)

                    propertyPagingDataChanged.call()
                }
            }
        }
    }

    init {
        searchText.observeForever(onSearchTextChangeObserver)

        updateProperties()
    }

    private fun updateProperties() {
        PropertyDataUpdater
            .update(application, appState.currentProject.id)
            .observeForever { workInfo ->
                if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                    refreshProperties()
                }
            }
    }

    fun refreshProperties() {
        onSearchTextChangeObserver.onChanged(searchText.value)
    }

    fun onPropertySelected(property: PropertyModel) {
        selectedProperty = property

        if (appState.currentProject.isExternalOnlyType &&
            listOf(PropertySurveyType.IE, PropertySurveyType.IESAP).contains(property.surveyType)
        ) {
            iePropertySelected.call()
        } else {
            surveyProperty()
        }
    }

    fun changePropertySurveyType() {
        selectedProperty.surveyType = PropertySurveyType.E
        selectedProperty.updatedAt = Instant.now()
        surveyProperty()
    }

    fun surveyProperty() {
        viewModelScope.launch(Dispatchers.IO) {
            propertyRepository.updateProperty(selectedProperty)
            appContainer.dataBackUpService.backupData(appState.currentProject.id)

            appState.currentProperty = selectedProperty
            appContainer.eventReportingService.reportPropertyEntered(selectedProperty.UPRN)

            navigateTo(PropertyListFragmentDirections.moveToSurveyListScreen())
        }
    }

    fun callNumber(number: String) = application.makePhoneCall(number)

    fun lookupAddress(address: String) = application.searchAddressOnMap(address)

    fun makeNoAccessEntry(property: PropertyModel) {
        navigateTo(
            PropertyListFragmentDirections.moveToNoAccessScreen(property.id, property.UPRN)
        )
    }

    fun getPhotoFileName(property: PropertyModel): String {
        return getFileName(
            requireNotNull(appState.profile).userName,
            property.UPRN,
            property.extBlockPhotos.size + 1,
            EXT_BLOCK_PHOTO_TAG
        )
    }

    fun onPhotoAdded(property: PropertyModel, filePath: String) {
        if (property.extPhotosClonedFrom.isNotEmpty()) {
            property.extPhotosClonedFrom = ""
            property.extBlockPhotos.clear()
        }

        property.extBlockPhotos.add(filePath)

        viewModelScope.launch(Dispatchers.IO) {
            appContainer.externalPhotosRepository.updateEntry(
                appState.currentProject.id,
                property,
                appState.profile?.fullName.orEmpty(),
                property.extBlockPhotos
            )
            appContainer.propertyRepository.updateProperty(property)
            appContainer.dataBackUpService.backupData(appState.currentProject.id)
        }
    }

    companion object {
        private const val PROPERTY_PAGE_SIZE = 50
        private const val PROPERTY_PREFETCH_DISTANCE = 5
    }
}
