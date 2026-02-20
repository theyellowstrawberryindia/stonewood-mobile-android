package uk.co.savills.stonewood.network.dto.datatransfer.elements

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RiskAssessmentSurveyElementDto(
    val id: Int,
    val elementId: String,
    val elementName: String,
    val expectedAnswer: Int,
    val elementExclude: Boolean,
)
