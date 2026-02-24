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
    val isRepairsAvailable: Boolean,
    val isClosed: Boolean = true,
    val numberOfExternalBlockPhotos: Int = 0
)
