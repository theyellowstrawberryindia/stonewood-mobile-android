package uk.co.savills.stonewood.network.dto.datatransfer.entries

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass

@SuppressLint("InvalidMethodName")
@JsonClass(generateAdapter = true)
class NoAccessEntryDto(
    val UPRN: String,
    val reasonForNoAccess: String,
    val description: String,
    val surveyorUserName: String,
    val images: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: String,
    val updatedAt: String
)
