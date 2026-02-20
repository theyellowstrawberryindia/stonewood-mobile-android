package uk.co.savills.stonewood.network.dto.datatransfer

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlterationDto(
    val noAlterations: Boolean,
    val alterations: String,
)
