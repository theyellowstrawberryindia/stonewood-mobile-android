package uk.co.savills.stonewood.network.dto.datatransfer.entries

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass
import uk.co.savills.stonewood.network.dto.datatransfer.PropertyDto

@SuppressLint("InvalidMethodName")
@JsonClass(generateAdapter = true)
data class CommunalDataDto(
    val id: Int?,
    val element: String,
    val propertyAddressUPRN: String,
    val propertyAddress: PropertyDto?,
    val surveyorUserName: String,
    val communalPartNumber: Int,
    val subElement: String,
    val subElementNumber: Int,
    val subElementDescription: String,
    val elementNotes: String,
    val repair: Boolean?,
    val repairDescription: String,
    val repairSpotPrice: Int?,
    val lifeRenewalBand: Int?,
    val lifeRenewalUnits: Int?,
    val asBuilt: Boolean?,
    val existingAgeBand: Int?,
    val images: String,
    val imageNoPhotoReason: String,
    val createdAt: String,
    val updatedAt: String,
    val syncId: Int?
)
