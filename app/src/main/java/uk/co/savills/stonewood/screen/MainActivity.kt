package uk.co.savills.stonewood.screen

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.model.AppUpdateType
import uk.co.savills.stonewood.screen.base.LocationTrackingActivity
import uk.co.savills.stonewood.util.connectionerror.ServerConnectionErrorHandler
import uk.co.savills.stonewood.util.customview.StandardDialog
import uk.co.savills.stonewood.util.hideKeyboard
import uk.co.savills.stonewood.util.navigation.NavigationEventObserver
import uk.co.savills.stonewood.util.navigation.goToAppStore

class MainActivity : LocationTrackingActivity(), ServerConnectionErrorHandler {
    private val viewModel: MainViewModel by viewModels()

    private val serverErrorDialog by lazy {
        StandardDialog.Builder(this)
            .setTitle(R.string.unexpected_api_error_dialog_header)
            .setPositiveButton(R.string.dialog_standard_button_text)
            .build()
    }

    private val noConnectionErrorDialog by lazy {
        StandardDialog.Builder(this)
            .setTitle(R.string.no_connection_dialog_header)
            .setDescription(R.string.no_connection_dialog_message)
            .setPositiveButton(R.string.dialog_standard_button_text)
            .build()
    }

    private val unexpectedErrorDialog by lazy {
        StandardDialog.Builder(this)
            .setTitle(R.string.unexpected_api_error_dialog_header)
            .setPositiveButton(R.string.dialog_standard_button_text)
            .build()
    }

    private val navController by lazy {
        try {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.navigationHostFragmentMain) as NavHostFragment
            navHostFragment.navController
        } catch (e: Exception) {
            Log.e("MainActivity", "Error getting navController", e)
            throw e
        }
    }

    private lateinit var appUpdateDialog: StandardDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d("MainActivity", "onCreate() started")

            setTheme(R.style.AppTheme)
            setContentView(R.layout.activity_main)

            Log.d("MainActivity", "Content view set, setting navigation graph")
            setNavigationGraph()

            Log.d("MainActivity", "Navigation graph set, setting bindings")
            setBindings()

            Log.d("MainActivity", "onCreate() completed successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Fatal error in onCreate", e)
            e.printStackTrace()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        try {
            if (!BuildConfig.DEBUG) {
                viewModel.evaluateAppVersion()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onStart", e)
        }
    }

    private fun setNavigationGraph() {
        try {
            val navGraph = navController.navInflater.inflate(R.navigation.navigation_graph)

            val destination = if (viewModel.isLoggedIn) {
                if (viewModel.shouldChangePassword) {
                    R.id.changePasswordFragment
                } else {
                    R.id.projectListFragment
                }
            } else {
                R.id.loginFragment
            }

            Log.d("MainActivity", "Setting start destination to: $destination")
            navGraph.setStartDestination(destination)
            navController.graph = navGraph
            Log.d("MainActivity", "Navigation graph set successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting navigation graph", e)
            throw e
        }
    }

    fun hideKeyboard(view: View) {
        try {
            currentFocus?.clearFocus()
            view.hideKeyboard()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error hiding keyboard", e)
        }
    }

    private fun setBindings() {
        try {
            viewModel.navigationEvent.observe(this, NavigationEventObserver(navController))

            viewModel.appUpdateAvailable.observe(this) { appUpdateType ->
                try {
                    if (::appUpdateDialog.isInitialized) {
                        appUpdateDialog.dismiss()
                    }

                    appUpdateDialog = getAppUpdateAvailableDialog(appUpdateType == AppUpdateType.MAJOR)
                    appUpdateDialog.show()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error showing app update dialog", e)
                }
            }

            Log.d("MainActivity", "Bindings set successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting bindings", e)
            throw e
        }
    }

    private fun getAppUpdateAvailableDialog(isMajorUpdate: Boolean): StandardDialog {
        val builder = StandardDialog.Builder(this)
            .setTitle(R.string.standard_dialog_header)
            .setDescription(R.string.app_update_available_dialog_message)
            .setPositiveButton(R.string.app_update_available_dialog_button_text) {
                try {
                    goToAppStore()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error going to app store", e)
                }
            }
            .setCancellable(!isMajorUpdate)

        if (!isMajorUpdate) {
            builder.setNegativeButton(R.string.dialog_skip_button_text)
        }

        return builder.build()
    }

    override fun onApiError(message: String) {
        try {
            serverErrorDialog
                .setDescription(message)
                .show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error showing server error dialog", e)
        }
    }

    override fun onNoConnectionError() {
        try {
            noConnectionErrorDialog.show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error showing no connection dialog", e)
        }
    }

    override fun onUnexpectedError(message: String) {
        try {
            val errorMessage = getString(R.string.unexpected_api_error_dialog_message, message)
            unexpectedErrorDialog
                .setDescription(errorMessage)
                .show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error showing unexpected error dialog", e)
        }
    }
}