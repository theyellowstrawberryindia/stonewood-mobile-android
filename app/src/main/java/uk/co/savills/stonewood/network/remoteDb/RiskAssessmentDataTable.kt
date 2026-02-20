@file:Suppress("SpellCheckingInspection")
@file:SuppressLint("InvalidMethodName")

package uk.co.savills.stonewood.network.remoteDb

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.entry.RiskAssessmentSurveyElementEntryModel

data class RiskAssessmentDataTable(
    val Id: Int?,
    val CreatedAt: String,
    val UpdatedAt: String,
    val UPRN: String,
    val SurveyorUserName: String,
    val SyncId: Int?,
    val ElementId: String,
    val ElementName: String,
    val ElementResult: String,
    val ElementDescription: String
) {
    companion object {
        fun from(
            model: RiskAssessmentSurveyElementEntryModel,
            surveyorName: String
        ): RiskAssessmentDataTable = with(model) {
            RiskAssessmentDataTable(
                null,
                details?.entryInstant?.toString().orEmpty(),
                details?.updateInstant?.toString().orEmpty(),
                details?.propertyUPRN.orEmpty(),
                surveyorName,
                null,
                elementId,
                question,
                answer.name.toLowerCase(),
                ""
            )
        }
    }
}
