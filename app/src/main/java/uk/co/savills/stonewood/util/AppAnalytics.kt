package uk.co.savills.stonewood.util

import android.util.Log
import androidx.annotation.WorkerThread
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.BuildConfig
import com.microsoft.appcenter.crashes.Crashes
import uk.co.savills.stonewood.MainApplication
import uk.co.savills.stonewood.PHOTO_DIRECTORY
import uk.co.savills.stonewood.model.survey.property.PropertyStatus
import java.io.File

class AppAnalytics {
    companion object {
        val tag = AppAnalytics.javaClass.name

        fun trackEvent(eventName: String) {
            Analytics.trackEvent(eventName, mapOf())
        }

        fun trackEvent(eventName: String, info: Map<String, String?>) {
            sanitize(info.toMutableMap())
            if (BuildConfig.DEBUG) {
                Log.i(tag, "Event logged: $eventName $info")
            } else {
                Analytics.trackEvent(eventName, info + getDefaultInfo())
            }
        }

        fun trackError(throwable: Throwable) {
            trackError(throwable, mapOf())
        }

        fun trackError(throwable: Throwable, info: Map<String, String?>) {
            sanitize(info.toMutableMap())
            if (BuildConfig.DEBUG) {
                Log.e(tag, "Exception logged: ${throwable.message}", throwable)
            } else {
                Crashes.trackError(throwable, info + getDefaultInfo(), listOf())
            }
        }

        private fun sanitize(info: MutableMap<String, String?>) {
            for (k in info.keys.filter { info[it] == null }) {
                info[k] = "null"
            }
        }

        @WorkerThread
        private fun getDefaultInfo(): Map<String, String> {
            val info = mutableMapOf<String, String>()

            if (!MainApplication.isAppInitialized || !MainApplication.appContainer.appState.isLoggedIn) {
                info["Surveyor"] = "Not logged in"
                return info
            }

            val appState = MainApplication.appContainer.appState

            info["Surveyor"] = requireNotNull(appState.profile).fullName

            val currentProject = appState.currentProject

            info["Project"] = currentProject.name

            val numberOfPhotos = File(MainApplication.app.filesDir, PHOTO_DIRECTORY).getPhotoCount(currentProject.id)
            info["Total photos"] = numberOfPhotos.toString()

            val completedPropertyCount = MainApplication.appContainer.propertyRepository
                .getProperties(currentProject.id)
                .filter { it.status == PropertyStatus.SURVEYED }.size
            info["Surveyed properties"] = completedPropertyCount.toString()

            return info
        }

        private fun File.getPhotoCount(projectId: String): Int {
            var photoCount = 0
            if (exists() && isDirectory) {
                listFiles()?.forEach { child ->
                    photoCount += if (child.isDirectory && child.name.startsWith(projectId)) {
                        child.getPhotoCount(projectId)
                    } else {
                        1
                    }
                }
            }
            return photoCount
        }
    }
}
