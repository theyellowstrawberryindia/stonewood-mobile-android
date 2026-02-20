package uk.co.savills.stonewood.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponseDto(
    @Json(name = "StatusCode") val statusCode: Int,
    @Json(name = "Message") val message: String,
)
