package uk.co.savills.stonewood.service

import android.util.Log
import com.microsoft.appcenter.crashes.Crashes
import uk.co.savills.stonewood.BuildConfig

class ErrorReportingService {
    fun reportError(exception: Exception, properties: Map<String, String> = mapOf()) {
        if (!BuildConfig.DEBUG) {
            Crashes.trackError(exception, properties, listOf())
        } else {
            Log.d("Error ---->", "${exception.message}: $properties")
        }
    }
}
