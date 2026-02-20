package uk.co.savills.stonewood.screen

import android.os.Bundle
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
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigationHostFragmentMain) as NavHostFragment

        navHostFragment.navController
    }

    private lateinit var appUpdateDialog: StandardDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        setNavigationGraph()
        setBindings()
    }

    override fun onStart() {
        super.onStart()

//        if (!BuildConfig.DEBUG) viewModel.evaluateAppVersion()
    }

    private fun setNavigationGraph() {
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_graph)

        navGraph.startDestination =
            if (viewModel.isLoggedIn) {
                if (viewModel.shouldChangePassword) R.id.changePasswordFragment else R.id.projectListFragment
            } else {
                R.id.loginFragment
            }

        navController.graph = navGraph
    }

    fun hideKeyboard(view: View) {
        currentFocus?.clearFocus()
        view.hideKeyboard()
    }

    private fun setBindings() {
        viewModel.navigationEvent.observe(this, NavigationEventObserver(navController))

        viewModel.appUpdateAvailable.observe(this) { appUpdateType ->
            if (::appUpdateDialog.isInitialized) appUpdateDialog.dismiss()

            appUpdateDialog = getAppUpdateAvailableDialog(appUpdateType == AppUpdateType.MAJOR)
            appUpdateDialog.show()
        }
    }

    private fun getAppUpdateAvailableDialog(isMajorUpdate: Boolean): StandardDialog {
        val builder = StandardDialog.Builder(this)
            .setTitle(R.string.standard_dialog_header)
            .setDescription(R.string.app_update_available_dialog_message)
            .setPositiveButton(R.string.app_update_available_dialog_button_text) {
                goToAppStore()
            }
            .setCancellable(!isMajorUpdate)

        if (!isMajorUpdate) {
            builder.setNegativeButton(R.string.dialog_skip_button_text)
        }

        return builder.build()
    }

    override fun onApiError(message: String) {
        serverErrorDialog
            .setDescription(message)
            .show()
    }

    override fun onNoConnectionError() = noConnectionErrorDialog.show()

    override fun onUnexpectedError(message: String) {
        val errorMessage = getString(R.string.unexpected_api_error_dialog_message, message)

        unexpectedErrorDialog
            .setDescription(errorMessage)
            .show()
    }
}
