package uk.co.savills.stonewood.network.dto.datatransfer

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PropertyStatsDto(
    val section: String,
    val uprn: String,
    val strata: String,
    val ta: String,
    val isCompleted: Boolean
)
