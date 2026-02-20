package uk.co.savills.stonewood.network.dto.datatransfer

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RenewalBandDto(
    val renewalYear: Int,
    val band: String,
    val firstGroup: Boolean,
    val startYear: Int,
    val endYear: Int,
    val field1: Int,
    val field2: Int,
    val renewalYearT: Int
)
