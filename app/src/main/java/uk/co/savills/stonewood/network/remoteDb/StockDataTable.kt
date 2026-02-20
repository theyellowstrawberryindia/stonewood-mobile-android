@file:Suppress("SpellCheckingInspection")
@file:SuppressLint("InvalidMethodName")

package uk.co.savills.stonewood.network.remoteDb

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.entry.StockSurveyElementEntryModel
import uk.co.savills.stonewood.util.mapper.mapToDto
import java.io.File

data class StockDataTable(
    val Id: Int?,
    val CreatedAt: String,
    val UpdatedAt: String,
    val UPRN: String,
    val SurveyorUserName: String,
    val SyncId: Int?,
    val CommunalPartNumber: Int,
    val SurveyType: String,
    val ElementSequence: Int,
    val Element: String,
    val SubElement: String,
    val SubElementNumber: Int?,
    val ElementNotes: String,
    val Repair: Boolean?,
    val RepairDescription: String,
    val RepairSpotPrice: Int?,
    val LifeRenewalBand: Int?,
    val LifeRenewalUnits: Int?,
    val AsBuilt: String,
    val ExistingAgeBand: Int?,
    val Images: String,
    val ImageNoPhoto: String,
    val isCloned: Boolean
) {
    companion object {
        fun from(
            model: StockSurveyElementEntryModel,
            surveyorName: String
        ): StockDataTable = with(model) {
            val userEntry =
                if (date?.isValid == true) date.toString() else subElementUserEntry

            StockDataTable(
                null,
                details?.entryInstant?.toString().orEmpty(),
                details?.updateInstant?.toString().orEmpty(),
                details?.propertyUPRN.orEmpty(),
                surveyorName,
                null,
                communalPartNumber,
                surveyType.title,
                sequenceNumber,
                title,
                subElement,
                subElementNumber,
                userEntry,
                repair,
                repairDescription,
                repairSpotPrice,
                lifeRenewalBand,
                lifeRenewalUnits,
                mapToDto(asBuilt),
                existingAgeBand,
                imagePaths.joinToString(";") { File(it).name },
                noAccessReason,
                isCloned ?: false,
            )
        }
    }
}
