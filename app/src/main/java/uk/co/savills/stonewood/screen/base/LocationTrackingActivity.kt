package uk.co.savills.stonewood.screen.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.model.LocationModel
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.customview.StandardDialog

open class LocationTrackingActivity : AppCompatActivity(), LocationTracker {

    private val locationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationRequest = LocationRequest().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = LOCATION_UPDATE_INTERVAL
        maxWaitTime = LOCATION_UPDATE_INTERVAL
        fastestInterval = LOCATION_UPDATE_INTERVAL
    }

    private var currentLocation: Location? = null

    private lateinit var monitorLocationTaskCompletionSource: TaskCompletionSource<Boolean>

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            with(locationResult.locations) {
                sortByDescending { it.accuracy }
                currentLocation = last()
            }
        }
    }

    private val locationPermissionRationaleDialog by lazy {
        AlertDialog.Builder(this)
            .setTitle(R.string.location_permission_rationale_dialog_title)
            .setMessage(R.string.location_permission_rationale_dialog_message)
            .setPositiveButton(R.string.settings_dialog_button) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID,
                    null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }.create()
    }

    private var isTrackingLocation: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(
            locationProviderChangedBroadcastReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )
    }

    override fun startMonitoringLocation(): Task<Boolean> {
        monitorLocationTaskCompletionSource = TaskCompletionSource()

        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        when {
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED -> requestLocationUpdates()

            shouldShowRequestPermissionRationale(permission) -> locationPermissionRationaleDialog.show()

            else -> requestPermissions(arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
        }

        return monitorLocationTaskCompletionSource.task
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        val settingsClient = LocationServices.getSettingsClient(this)
        val checkLocationSettingsTask =
            settingsClient.checkLocationSettings(locationSettingsRequest)

        checkLocationSettingsTask.addOnSuccessListener {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )

            monitorLocationTaskCompletionSource.trySetResult(true)
            isTrackingLocation = true
        }

        checkLocationSettingsTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                exception.startResolutionForResult(this, LOCATION_SETTINGS_RESOLUTION_CODE)
            }
        }
    }

    override fun getCurrentLocation(): LocationModel {
        return if (currentLocation == null) {
            LocationModel()
        } else {
            LocationModel.from(requireNotNull(currentLocation))
        }
    }

    override fun stopMonitoringLocation() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        isTrackingLocation = false
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                requestLocationUpdates()
            } else {
                monitorLocationTaskCompletionSource.trySetResult(false)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            LOCATION_SETTINGS_RESOLUTION_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    requestLocationUpdates()
                } else {
                    if (isTrackingLocation) {
                        locationProviderDisabledDialog.show()
                    } else {
                        monitorLocationTaskCompletionSource.trySetResult(false)
                    }
                }
            }
        }
    }

    private val locationProviderChangedBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                val isGPSAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (isTrackingLocation) {
                    if (!isGPSAvailable && !locationProviderDisabledDialog.isShowing) {
                        locationProviderDisabledDialog.show()
                    } else if (isGPSAvailable && locationProviderDisabledDialog.isShowing) {
                        locationProviderDisabledDialog.dismiss()
                    }
                }
            }
        }
    }

    private val locationProviderDisabledDialog by lazy {
        StandardDialog.Builder(this)
            .setTitle(R.string.location_provider_rationale_dialog_title)
            .setDescription(R.string.location_provider_rationale_dialog_message)
            .setPositiveButton(R.string.dialog_standard_button_text) {
                requestLocationUpdates()
            }
            .setCancellable(false)
            .build()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 0
        private const val LOCATION_UPDATE_INTERVAL = 2000L
        private const val LOCATION_SETTINGS_RESOLUTION_CODE = 10001
    }
}
