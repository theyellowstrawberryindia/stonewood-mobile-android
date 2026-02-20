package uk.co.savills.stonewood.network.dto.datatransfer

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgeBandDto(
    val ageYear: Int,
    val band: String,
    val firstGroup: Boolean,
    val startYear: Int,
    val endYear: Int,
    val ageYearT: Int
)
