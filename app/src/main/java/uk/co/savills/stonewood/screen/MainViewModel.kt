package uk.co.savills.stonewood.screen

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.NavigationGraphDirections
import uk.co.savills.stonewood.model.AppUpdateType
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.util.Result
import uk.co.savills.stonewood.util.SingleLiveEvent

class MainViewModel(application: Application) : BaseViewModel(application) {

    val isLoggedIn
        get() = appContainer.authService.isLoggedIn

    val shouldChangePassword
        get() = appState?.profile?.defaultPassword ?: false

    init {
        appContainer.authService.sessionInvalidatedEvent.observeForever {
            navigateToLogin()
        }
    }

    val appUpdateAvailable = SingleLiveEvent<AppUpdateType>()

    fun evaluateAppVersion() {
        val currentVersion = BuildConfig.VERSION_NAME.toAppVersion()

        viewModelScope.launch(Dispatchers.IO) {
            val result = apiService.getAppVersion()

            if (result is Result.Success) {
                val minimumCompatibleVersion = result.data.minimumCompatible.toAppVersion()
                val latestVersion = result.data.latest.toAppVersion()

                when {
                    minimumCompatibleVersion.isVersionGreaterThan(currentVersion) -> {
                        appUpdateAvailable.postValue(AppUpdateType.MAJOR)
                    }

                    latestVersion.isVersionGreaterThan(currentVersion) -> {
                        appUpdateAvailable.postValue(AppUpdateType.MINOR)
                    }
                }
            }
        }
    }

    private fun List<Int>.isVersionGreaterThan(version: List<Int>): Boolean {
        if (this[0] > version[0]) {
            return true
        } else if (this[0] == version[0]) {
            if (this[1] > version[1]) {
                return true
            } else if (this[1] == version[1]) {
                if (this[2] > version[2]) return true
            }
        }

        return false
    }

    private fun String.toAppVersion(): List<Int> {
        return split(".").map { it.toInt() }
    }

    private fun navigateToLogin() {
        navigateTo(NavigationGraphDirections.moveToLoginFragment())
    }
}
