package uk.co.savills.stonewood.network.dto.datatransfer.elements

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QualityStandardSurveyElementDto(
    val id: Int,
    val elementId: String,
    val elementName: String,
    val elementExclude: Boolean,
)
