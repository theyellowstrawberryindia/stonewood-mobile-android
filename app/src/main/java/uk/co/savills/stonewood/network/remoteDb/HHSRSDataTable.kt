@file:Suppress("ClassName", "SpellCheckingInspection")
@file:SuppressLint("InvalidClassName", "InvalidMethodName")

package uk.co.savills.stonewood.network.remoteDb

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.entry.HHSRSSurveyElementEntryModel
import java.io.File

data class HHSRSDataTable(
    val Id: Int?,
    val CreatedAt: String,
    val UpdatedAt: String,
    val SurveyorUserName: String,
    val SyncId: Int?,
    val ElementId: String,
    val Element: String,
    val ElementRating: String,
    val RatingDescription: String,
    val RatingCost: String,
    val Images: String,
    val InternalLocation: String,
    val ExternalLocation: String,
    val ChangedToTypical: String?,
) {
    companion object {
        fun from(model: HHSRSSurveyElementEntryModel, surveyorName: String): HHSRSDataTable =
            with(model) {
                HHSRSDataTable(
                    null,
                    details?.entryInstant?.toString().orEmpty(),
                    details?.updateInstant?.toString().orEmpty(),
                    surveyorName,
                    null,
                    id,
                    name,
                    rating?.name?.toLowerCase()?.capitalize().orEmpty(),
                    ratingDescription,
                    ratingCost,
                    imagePaths.joinToString(";") { File(it).name },
                    internalLocations.joinToString(";"),
                    externalLocations.joinToString(";"),
                    changedToTypicalFrom?.title
                )
            }
    }
}
