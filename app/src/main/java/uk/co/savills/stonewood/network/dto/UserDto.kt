package uk.co.savills.stonewood.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val userName: String,
    val workEmail: String,
    val defaultPasswordChanged: Boolean,
    val token: String,
    val refreshToken: String,
    val expiryDateTime: String,
)
