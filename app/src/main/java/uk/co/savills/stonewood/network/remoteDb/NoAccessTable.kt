@file:Suppress("SpellCheckingInspection")
@file:SuppressLint("InvalidMethodName")

package uk.co.savills.stonewood.network.remoteDb

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.noaccess.NoAccessEntryModel
import java.io.File

data class NoAccessTable(
    val Id: Int?,
    val CreatedAt: String,
    val UpdatedAt: String,
    val UPRN: String,
    val ReasonForNoAccess: String,
    val Description: String,
    val SurveyorUserName: String,
    val SyncId: Int?,
    val Images: String,
    val Latitude: Double,
    val Longitude: Double
) {
    companion object {
        fun from(model: NoAccessEntryModel, surveyorName: String): NoAccessTable = with(model) {
            NoAccessTable(
                null,
                createdAt.toString(),
                createdAt.toString(),
                UPRN,
                reason,
                remarks,
                surveyorName,
                null,
                imagePaths.joinToString(";") { filePath -> File(filePath).name },
                location.latitude,
                location.longitude
            )
        }
    }
}
