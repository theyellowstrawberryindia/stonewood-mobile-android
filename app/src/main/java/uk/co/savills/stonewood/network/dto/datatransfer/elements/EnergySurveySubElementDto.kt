package uk.co.savills.stonewood.network.dto.datatransfer.elements

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EnergySurveySubElementDto(
    val responseOrder: Int,
    val responseId: String?,
    val response: String?,
    val responseDescription: String?,
    val skipCodes: String?,
    val isRareItem: Boolean
)
