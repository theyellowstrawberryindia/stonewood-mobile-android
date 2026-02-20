package uk.co.savills.stonewood.network.dto.datatransfer

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass

@SuppressLint("InvalidMethodName")
@JsonClass(generateAdapter = true)
class PropertyDto(
    val id: Int,
    val order: String?,
    val uprn: String,
    val ta: String,
    val strata: String?,
    val number: String?,
    val address1: String,
    val address2: String?,
    val address3: String?,
    val address4: String?,
    val postCode: String?,
    val originalSurveyType: String,
    val surveyType: String,
    val section: String,
    val contactNumber1: String?,
    val contactNumber2: String?,
    val contactNotes: String?,
    val frontDoorPhoto: String?,
    val extBlockElevationPhotos: String?,
    val extBlockElevationPhotosClonedFromUPRN: String?,
    val hasExternalPhoto: Boolean,
    val latitude: Double,
    val longitude: Double,
    val isDeleted: Boolean,
    val createdAt: String?,
    val updatedAt: String?,
)
