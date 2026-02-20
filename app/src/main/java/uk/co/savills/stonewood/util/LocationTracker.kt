package uk.co.savills.stonewood.util

import com.google.android.gms.tasks.Task
import uk.co.savills.stonewood.model.LocationModel

interface LocationTracker {
    fun startMonitoringLocation(): Task<Boolean>
    fun getCurrentLocation(): LocationModel
    fun stopMonitoringLocation()
}
