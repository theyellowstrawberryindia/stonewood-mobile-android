package uk.co.savills.stonewood.screen.survey.contact

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.util.getNonNullValue
import uk.co.savills.stonewood.util.navigation.makePhoneCall
import java.time.Instant

class ContactDetailViewModel(application: Application) : BaseViewModel(application) {
    val property: PropertyModel
        get() = appContainer.appState.currentProperty

    val contact = property.contact
    val contactNotes = MutableLiveData(contact.notes)

    fun saveNotes() {
        property.contact.notes = contactNotes.getNonNullValue()
        property.updatedAt = Instant.now()
        viewModelScope.launch(Dispatchers.IO) {
            appContainer.propertyRepository.updateProperty(property)
            appContainer.dataBackUpService.backupData(appState.currentProject.id)
        }
    }

    fun onContactNumberClick() {
        application.makePhoneCall(property.contact.number)
    }

    fun onSecondaryContactNumberClick() {
        application.makePhoneCall(property.contact.numberSecondary)
    }
}
