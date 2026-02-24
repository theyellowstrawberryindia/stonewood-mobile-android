package uk.co.savills.stonewood

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log

class MainApplication : Application() {

    val appContainer by lazy {
        try {
            AppContainer(applicationContext) { isNetworkAvailable }
        } catch (e: Exception) {
            Log.e("MainApplication", "Error creating AppContainer", e)
            throw e
        }
    }

    private val connectivityManager by lazy {
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private var isNetworkAvailable = false

    override fun onCreate() {
        super.onCreate()

        try {
            Log.d("MainApplication", "Application onCreate() called")

            _appContext = applicationContext
            _app = this
            isAppInitialized = true

            Log.d("MainApplication", "Registering network callback")
            connectivityManager.registerDefaultNetworkCallback(networkCallback)

            Log.d("MainApplication", "MainApplication initialized successfully")
        } catch (e: Exception) {
            Log.e("MainApplication", "Fatal error in onCreate", e)
            e.printStackTrace()
            isAppInitialized = false
        }
    }

    override fun onTerminate() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            Log.d("MainApplication", "Network callback unregistered")
        } catch (e: Exception) {
            Log.e("MainApplication", "Error unregistering network callback", e)
        }

        super.onTerminate()
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isNetworkAvailable = true
            Log.d("MainApplication", "Network available")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isNetworkAvailable = false
            Log.d("MainApplication", "Network lost")
        }

        override fun onUnavailable() {
            super.onUnavailable()
            isNetworkAvailable = false
            Log.d("MainApplication", "Network unavailable")
        }
    }

    companion object {
        var isAppInitialized: Boolean = false

        @SuppressLint("StaticFieldLeak")
        private var _appContext: Context? = null
        val appContext: Context?
            get() = _appContext

        @SuppressLint("StaticFieldLeak")
        private var _app: MainApplication? = null
        val app: MainApplication
            get() = requireNotNull(_app) {
                "Application not initialized. Make sure MainApplication is set in AndroidManifest.xml"
            }

        val appContainer: AppContainer
            get() {
                if (_app == null) {
                    throw IllegalStateException("MainApplication not initialized")
                }
                return app.appContainer
            }
    }
}