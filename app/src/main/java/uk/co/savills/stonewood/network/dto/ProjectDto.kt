package uk.co.savills.stonewood.network.dto

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass

@SuppressLint("InvalidMethodName")
@JsonClass(generateAdapter = true)
data class ProjectDto(
    val id: String,
    val name: String,
    val description: String?,
    val stockConditionSurvey: Boolean,
    val stockConditionSurveyName: String?,
    val hhsrsSurvey: Boolean,
    val hhsrsSurveyName: String?,
    val qualityStandardSurvey: Boolean,
    val qualityStandardSurveyName: String?,
    val riskAssessmentSurvey: Boolean,
    val riskAssessmentSurveyName: String?,
    val energySurveyName: String?,
    val externalOnlyAvailable: Boolean,
    val isRepairsAvailable: Boolean = false,
    val isClosed: Boolean = true,
    val numberOfExternalBlockPhotos: Int = 0
)
//{
//        "name": "swapTestProject",
//        "description": null,
//        "riskAssessmentSurvey": true,
//        "riskAssessmentSurveyName": null,
//        "qualityStandardSurvey": true,
//        "qualityStandardSurveyName": null,
//        "energySurveyName": null,
//        "stockConditionSurvey": true,
//        "stockConditionSurveyName": null,
//        "hhsrsSurvey": true,
//        "hhsrsSurveyName": null,
//        "externalOnlyAvailable": false,
//        "stockSurveyExcelSynced": true,
//        "id": 3,
//        "createdAt": "2025-07-14T10:29:04.082804Z",
//        "updatedAt": "2025-07-14T10:29:04.082804Z"
//    }