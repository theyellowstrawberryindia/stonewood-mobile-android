package uk.co.savills.stonewood.network.dto.datatransfer.entries

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass
import uk.co.savills.stonewood.network.dto.datatransfer.PropertyDto

@SuppressLint("InvalidMethodName")
@JsonClass(generateAdapter = true)
data class ExtBlockPhotoDto(
    val id: Int?,
    val propertyAddressUPRN: String,
    val propertyAddress: PropertyDto?,
    val images: String,
    val surveyorUserName: String,
    val syncId: Int?,
    val createdAt: String,
    val updatedAt: String,
)
