package uk.co.savills.stonewood.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmailAttachmentDto(
    val filename: String,
    val type: String,
    val content: String
)
