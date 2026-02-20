package uk.co.savills.stonewood.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthRequestDto(
    val email: String,
    val password: String
)
