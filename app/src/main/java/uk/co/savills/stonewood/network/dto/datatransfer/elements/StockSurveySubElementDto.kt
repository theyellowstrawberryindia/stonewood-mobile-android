package uk.co.savills.stonewood.network.dto.datatransfer.elements

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StockSurveySubElementDto(
    val id: Int,
    val subElementNumber: Int,
    val subElement: String,
    val skipElements: String?,
    val life: Int,
    val minimumNumberPhotosRequired: Int,
    val costHouse: Double,
    val costBungalow: Double,
    val costFlat: Double,
    val costBlock: Double
)
