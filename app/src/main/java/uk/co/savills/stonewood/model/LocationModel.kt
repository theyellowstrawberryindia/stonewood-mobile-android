package uk.co.savills.stonewood.model

import android.location.Location

data class LocationModel(
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
) {
    companion object {
        fun from(location: Location): LocationModel = with(location) {
            return LocationModel(longitude, latitude)
        }
    }
}
