package uk.co.savills.stonewood.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PasswordChangeRequestDto(
    val password: String,
    val newPassword: String
)
