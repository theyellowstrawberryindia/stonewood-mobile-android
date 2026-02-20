package uk.co.savills.stonewood.util

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import uk.co.savills.stonewood.BuildConfig

class WifiLocker(
    private val application: Application
) {
    private var wifiLock: WifiManager.WifiLock? = null

    fun holdLock() {
        val wifiManager =
            application.getSystemService(Context.WIFI_SERVICE) as? WifiManager ?: return

        if (wifiLock == null) {
            wifiLock =
                wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, WIFI_LOCK_TAG)
                    .apply {
                        setReferenceCounted(false)
                    }
        }

        if (wifiLock != null && !requireNotNull(wifiLock).isHeld) {
            wifiLock?.acquire()
        }
    }

    fun releaseLock() {
        wifiLock?.release()
    }

    companion object {
        private const val WIFI_LOCK_TAG = "${BuildConfig.APPLICATION_ID}.wifi_lock"
    }
}
