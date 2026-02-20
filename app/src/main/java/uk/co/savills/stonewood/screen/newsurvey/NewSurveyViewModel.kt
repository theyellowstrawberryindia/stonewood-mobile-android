package uk.co.savills.stonewood.screen.newsurvey

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.LocationModel
import uk.co.savills.stonewood.model.survey.property.AddressModel
import uk.co.savills.stonewood.model.survey.property.ContactModel
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.model.survey.property.PropertySurveyType
import uk.co.savills.stonewood.repository.property.PropertyRepository
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.getNonNullValue
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class NewSurveyViewModel(application: Application) : BaseViewModel(application) {

    private val _uprn = MutableLiveData<String>()
    val uprn: LiveData<String>
        get() = _uprn

    private val _canCreateSurvey = MutableLiveData(false)
    val canCreateSurvey: LiveData<Boolean>
        get() = _canCreateSurvey

    var number = MutableLiveData("")
    var addressLine1 = MutableLiveData("")
    var addressLine2 = MutableLiveData("")
    var addressLine3 = MutableLiveData("")
    var addressLine4 = MutableLiveData("")
    var postalCode = MutableLiveData("")

    val propertyAdded = SingleLiveEvent<Nothing?>()

    private val propertyRepository: PropertyRepository
        get() = appContainer.propertyRepository

    init {
        number.observeForever { evaluateCanCreateSurvey() }
        addressLine1.observeForever { evaluateCanCreateSurvey() }
        postalCode.observeForever { evaluateCanCreateSurvey() }

        generateUprn()
    }

    private fun generateUprn() {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = appState.profile

            if (profile == null) {
                appContainer.authService.invalidateSession()
            } else {
                var surveyorInitials = profile.firstName.first().toString()

                if (profile.lastName.isNotBlank()) {
                    surveyorInitials += profile.lastName.first()
                }

                val formatter = DateTimeFormatter
                    .ofPattern("HHmmss")
                    .withZone(ZoneId.from(ZoneOffset.UTC))
                val time = formatter.format(LocalDateTime.now())

                _uprn.postValue("${surveyorInitials}_$time")
            }
        }
    }

    fun createSurvey() {
        _isBusy.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val properties = propertyRepository.getProperties(appState.currentProject.id)

            val id = properties.maxByOrNull { it.id }?.id?.plus(100000) ?: 100000
            val order = 99
            val surveyType = PropertySurveyType.EC

            val property = PropertyModel(
                id,
                order,
                uprn.getNonNullValue(),
                "BLOCK",
                "BLOCK",
                AddressModel(
                    number.getNonNullValue(),
                    addressLine1.getNonNullValue(),
                    addressLine2.getNonNullValue(),
                    addressLine3.getNonNullValue(),
                    addressLine4.getNonNullValue(),
                    postalCode.getNonNullValue()
                ),
                surveyType,
                surveyType,
                "9999",
                ContactModel("", "", ""),
                frontDoorPhoto = "",
                LocationModel(),
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            propertyRepository.insertProperties(listOf(property))
            appContainer.dataBackUpService.backupData(appState.currentProject.id)
        }.invokeOnCompletion {
            _isBusy.postValue(true)
            propertyAdded.call()
        }
    }

    private fun evaluateCanCreateSurvey() {
        _canCreateSurvey.value = number.getNonNullValue().isNotBlank() &&
            addressLine1.getNonNullValue().isNotBlank() &&
            postalCode.getNonNullValue().isNotBlank()
    }
}
