package uk.co.savills.stonewood.screen.survey.survey

import android.app.Application
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.screen.base.BaseViewModel

class SurveyTabViewModel(application: Application) : BaseViewModel(application) {
    private val property = appContainer.appState.currentProperty

    val surveys
        get() = appState.surveys

    lateinit var addressText: String

    init {
        setAddressText()
    }

    private fun setAddressText() {
        var addressText = "${property.UPRN},"
        val address = property.address

        if (address.number.isNotBlank()) addressText += " ${address.number}"

        addressText += " ${address.line1}"

        if (address.postalCode.isNotBlank()) addressText += " - ${address.postalCode}"

        this.addressText = addressText
    }

    fun isEnabled(survey: SurveyModel) = property.isEnabled(appState.currentProject, survey, surveys)
}
