package uk.co.savills.stonewood.network.dto.datatransfer

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoNoAccessReasonDto(
    val id: String,
    val reason: String,
)
