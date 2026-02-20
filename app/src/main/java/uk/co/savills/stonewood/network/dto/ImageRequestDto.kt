package uk.co.savills.stonewood.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageRequestDto(
    val surveyorUserName: String,
    val syncId: Int,
    val fileName: String,
)
