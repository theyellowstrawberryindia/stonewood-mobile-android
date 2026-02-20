package uk.co.savills.stonewood.network.dto.datatransfer.entries

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass

@SuppressLint("InvalidClassName, InvalidMethodName")
@JsonClass(generateAdapter = true)
data class HHSRSSurveyElementEntryDto(
    val elementId: String,
    val element: String,
    val elementRating: String,
    val ratingDescription: String,
    val ratingCost: String,
    val images: String,
    val internalLocation: String,
    val externalLocation: String,
    val changedToTypical: String?,
    val propertyAddressUPRN: String,
    val surveyorUserName: String,
    val createdAt: String,
    val updatedAt: String,
)
