package uk.co.savills.stonewood.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppVersionDto(
    val minimumCompatibleAppVersion: String,
    val latestAppVersion: String
)
