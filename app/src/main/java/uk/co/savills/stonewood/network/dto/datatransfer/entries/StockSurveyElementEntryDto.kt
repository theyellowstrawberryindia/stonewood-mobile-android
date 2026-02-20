package uk.co.savills.stonewood.network.dto.datatransfer.entries

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass

@SuppressLint("InvalidMethodName")
@JsonClass(generateAdapter = true)
class StockSurveyElementEntryDto(
    val communalPartNumber: Int,
    val surveyType: String,
    val elementSequence: Int,
    val element: String,
    val subElement: String,
    val subElementNumber: Int,
    val subElementDescription: String,
    val elementNotes: String,
    val repair: Boolean?,
    val repairDescription: String,
    val repairSpotPrice: Int?,
    val lifeRenewalBand: Int?,
    val lifeRenewalUnits: Int?,
    val asBuilt: String,
    val existingAgeBand: Int?,
    val images: String,
    val imageNoPhotoReason: String,
    val isCloned: Boolean,
    val propertyAddressUPRN: String,
    val surveyorUserName: String,
    val createdAt: String,
    val updatedAt: String,
)
