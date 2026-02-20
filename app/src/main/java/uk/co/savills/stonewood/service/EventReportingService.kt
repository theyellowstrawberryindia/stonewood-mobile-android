package uk.co.savills.stonewood.service

import android.app.Application
import android.util.Log
import com.microsoft.appcenter.analytics.Analytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.AppContainer
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.MainApplication
import uk.co.savills.stonewood.util.AppAnalytics

class EventReportingService(
    private val application: Application
) {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(serviceJob + Dispatchers.IO)

    private val appContainer: AppContainer
        get() = (application as MainApplication).appContainer

    fun reportPropertyEntered(uprn: String) {
        reportEvent("Property entered", mapOf("Property UPRN" to uprn))
    }

    fun reportPropertySurveyed(uprn: String) {
        reportEvent("Property surveyed", mapOf("Property UPRN" to uprn))
    }

    fun reportProjectSelected() {
        reportEvent("Project selected")
    }

    fun reportStatisticsAccessed() {
        serviceScope.launch {
            val name = "Statistics accessed"
            val info = mutableMapOf(
                "Project" to appContainer.appState.currentProject.name,
                "Surveyor" to appContainer.appState.profile?.fullName
            )

            if (BuildConfig.DEBUG) {
                Log.d("EventReportingService: $name", info.toString())
            } else {
                Analytics.trackEvent(name, info)
            }
        }
    }

    private fun reportEvent(name: String, info: Map<String, String> = mapOf()) {
        serviceScope.launch {
            AppAnalytics.trackEvent(name, info)
        }
    }
}
