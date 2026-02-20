package uk.co.savills.stonewood.model.survey.entry

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.property.AddressModel
import java.time.Instant

@SuppressLint("InvalidMethodName")
data class CommunalDataModel(
    var id: Int?,
    val element: String,
    val propertyUPRN: String,
    val address: AddressModel,
    val surveyor: String,
    val communalPartNumber: Int,
    var subElementNumber: Int,
    var subElement: String,
    var subElementUserEntry: String,
    var description: String,
    var repair: Boolean?,
    var repairDescription: String,
    var repairSpotPrice: Int?,
    var lifeRenewalBand: Int?,
    var lifeRenewalUnits: Int?,
    var asBuilt: Boolean?,
    val imagePaths: MutableList<String>,
    var noAccessReason: String,
    var existingAgeBand: Int?,
    val createdAt: Instant,
    val syncId: Int?
)
