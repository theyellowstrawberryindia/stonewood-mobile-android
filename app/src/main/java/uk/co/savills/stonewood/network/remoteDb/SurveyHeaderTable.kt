@file:Suppress("SpellCheckingInspection")
@file:SuppressLint("InvalidMethodName")

package uk.co.savills.stonewood.network.remoteDb

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import java.io.File

data class SurveyHeaderTable(
    val UPRN: String,
    val Id: Int,
    val CreatedAt: String,
    val UpdateAt: String,
    val Order: Int,
    val BPRN: String,
    val cBPRN: String,
    val TA: String,
    val Strata: String,
    val Number: String,
    val Address1: String,
    val Address2: String,
    val Address3: String,
    val Address4: String,
    val PostCode: String,
    val OriginalSurveyType: String,
    val SurveyType: String,
    val Section: String,
    val ContactNumber1: String,
    val ContactNumber2: String,
    val ContactNotes: String,
    val SyncId: Int?,
    val Latitude: Double,
    val Longitiude: Double,
    val FrontDoorPhoto: String,
    val Status: String,
    val IsDeleted: Boolean,
    val IsSynced: Boolean?,
    val ExtBlockElevationPhotos: String,
    val ExtBlockElevationPhotosClonedFrom: String,
    val HasExternalPhoto: Boolean
) {
    companion object {
        fun from(model: PropertyModel): SurveyHeaderTable = with(model) {
            SurveyHeaderTable(
                UPRN,
                id,
                createdAt?.toString().orEmpty(),
                updatedAt?.toString().orEmpty(),
                order,
                "",
                "",
                TA,
                strata,
                address.number,
                address.line1,
                address.line2,
                address.line3,
                address.line4,
                address.postalCode,
                surveyTypeOriginal.name,
                surveyType.name,
                section,
                contact.number,
                contact.numberSecondary,
                contact.notes,
                null,
                location.latitude,
                location.longitude,
                File(frontDoorPhoto).name,
                if (surveyStatus.areRequiredSurveysComplete) "Finished" else "InProgress",
                isDeleted,
                null,
                extBlockPhotos.joinToString(";") { File(it).name },
                extPhotosClonedFrom,
                hasExternalPhoto
            )
        }
    }
}
