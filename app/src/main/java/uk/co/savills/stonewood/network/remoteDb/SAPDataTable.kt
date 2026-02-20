@file:Suppress("ClassName", "SpellCheckingInspection")
@file:SuppressLint("InvalidClassName", "InvalidMethodName")

package uk.co.savills.stonewood.network.remoteDb

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.entry.EnergySurveyElementEntryModel

data class SAPDataTable(
    val Id: Int?,
    val CreatedAt: String,
    val UpdatedAt: String,
    val UPRN: String,
    val SurveyorUserName: String,
    val SyncId: Int?,
    val QuestionId: Int,
    val Question: String,
    val ResponseId: String,
    val Response: String
) {
    companion object {
        fun from(
            model: EnergySurveyElementEntryModel,
            surveyorName: String
        ): SAPDataTable = with(model) {
            SAPDataTable(
                null,
                details?.entryInstant?.toString().orEmpty(),
                details?.updateInstant?.toString().orEmpty(),
                details?.propertyUPRN.orEmpty(),
                surveyorName,
                null,
                elementId,
                element,
                subElementId,
                subElement
            )
        }
    }
}
