package uk.co.savills.stonewood.model.survey

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.entry.HHSRSSurveyElementEntryModel

@SuppressLint("InvalidClassName")
data class HHSRSSevereIssueModel(
    val surveyorId: Int,
    val projectId: String,
    val propertyId: Int,
    val elementId: String,
    val elementName: String,
    val remarks: String,
    val attachments: List<String>,
    val internalLocations: List<String>,
    val externalLocations: List<String>,
) {
    var isReported = false

    companion object {
        fun from(
            entry: HHSRSSurveyElementEntryModel,
            surveyorId: Int,
            projectId: String,
            propertyId: Int
        ): HHSRSSevereIssueModel {
            return with(entry) {
                HHSRSSevereIssueModel(
                    surveyorId,
                    projectId,
                    propertyId,
                    id,
                    name,
                    ratingDescription,
                    imagePaths,
                    internalLocations,
                    externalLocations
                )
            }
        }
    }
}
