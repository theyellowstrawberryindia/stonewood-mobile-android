package uk.co.savills.stonewood.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshAuthTokenRequestDto(
    val refreshToken: String,
    val accessToken: String,
)
