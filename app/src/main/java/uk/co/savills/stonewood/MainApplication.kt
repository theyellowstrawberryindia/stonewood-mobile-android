package uk.co.savills.stonewood

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network

class MainApplication : Application() {
    val appContainer by lazy { AppContainer(applicationContext) { isNetworkAvailable } }

    private val connectivityManager by lazy {
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private var isNetworkAvailable = false

    override fun onCreate() {
        super.onCreate()

        _appContext = applicationContext
        _app = this
        isAppInitialized = true

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onTerminate() {
        connectivityManager.unregisterNetworkCallback(networkCallback)

        super.onTerminate()
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            isNetworkAvailable = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)

            isNetworkAvailable = false
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
            get() = requireNotNull(_app)

        val appContainer: AppContainer
            get() = app.appContainer
    }
}
