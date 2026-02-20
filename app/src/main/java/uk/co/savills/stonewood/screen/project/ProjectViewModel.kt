package uk.co.savills.stonewood.screen.project

import android.app.Application
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.navigation.makePhoneCall

class ProjectViewModel(
    application: Application,
    private val locationTracker: LocationTracker
) : BaseViewModel(application) {

    val projectName: String
        get() = appContainer.appState.currentProject.name

    val helpContact = "07971 223 489"

    fun initialize() {
        locationTracker.stopMonitoringLocation()
    }

    fun conductSurvey() {
        locationTracker.startMonitoringLocation().addOnSuccessListener { isTrackingLocation ->
            if (isTrackingLocation) {
                navigateTo(ProjectFragmentDirections.moveToPropertyListScreen())
            }
        }
    }

    fun transferData() {
        navigateTo(ProjectFragmentDirections.moveToDataTransferScreen())
    }

    fun createNewSurvey() {
        navigateTo(ProjectFragmentDirections.moveToNewSurveyScreen())
    }

    fun onHelpContactClick() = application.makePhoneCall(helpContact)

    fun viewSurveyStatistics() {
        appContainer.eventReportingService.reportStatisticsAccessed()
        navigateTo(ProjectFragmentDirections.moveToStatisticsFragment())
    }
}
