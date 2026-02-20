package uk.co.savills.stonewood.network.dto.datatransfer.elements

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class EnergySurveyElementDto(
    val questionId: Int,
    val questionOrder: Int,
    val grouping: String,
    val subHeading: String,
    val displayQuestion: String,
    val question: String,
    val responseType: String,
    val specialScreenExtraHeader: String?,
    val warnValueHigh: Int?,
    val warnValueLow: Int?,
    val limitValueHigh: Int?,
    val energyQuestionVarients: List<EnergySurveySubElementDto>
)
