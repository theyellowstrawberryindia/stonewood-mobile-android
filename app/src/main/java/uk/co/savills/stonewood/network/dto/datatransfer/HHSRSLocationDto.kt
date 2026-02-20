package uk.co.savills.stonewood.network.dto.datatransfer

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass

@SuppressLint("InvalidClassName")
@JsonClass(generateAdapter = true)
data class HHSRSLocationDto(
    val id: Int,
    val locationName: String,
    val locationType: Int,
)
