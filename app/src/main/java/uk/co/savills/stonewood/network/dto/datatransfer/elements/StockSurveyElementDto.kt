package uk.co.savills.stonewood.network.dto.datatransfer.elements

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StockSurveyElementDto(
    val id: Int,
    val elementSequence: Int,
    val surveyCategory: String,
    val elementGroup: String,
    val surveyType: String,
    val element: String,
    val dwellingUom: String,
    val blockUom: String,
    val communalElement: Boolean?,
    val warnValueHigh: Int?,
    val warnValueLow: Int?,
    val useQuantityAdder: Boolean?,
    val useQuantityMultiplier: Boolean?,
    @Json(name = "disableDoCFiltering") val disableAgeBandFiltering: Boolean?,
    val asBuilt: Boolean?,
    val stockQuestionVarients: List<StockSurveySubElementDto>
)
