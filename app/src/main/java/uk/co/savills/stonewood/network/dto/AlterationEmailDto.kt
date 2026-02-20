package uk.co.savills.stonewood.network.dto

import com.squareup.moshi.JsonClass
import uk.co.savills.stonewood.network.dto.datatransfer.AlterationDto

@JsonClass(generateAdapter = true)
data class AlterationEmailDto(
    val surveyorId: Int,
    val propertyAddressIds: List<Int>,
    val alteration: AlterationDto
)
