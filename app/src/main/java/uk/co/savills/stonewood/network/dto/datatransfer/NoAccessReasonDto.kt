package uk.co.savills.stonewood.network.dto.datatransfer

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NoAccessReasonDto(
    val reason: String,
)
