package uk.co.savills.stonewood.network.dto.datatransfer.elements

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ValidationElementDto(
    val id: Int,
    val operand: String,
    val groupName: String,
    val leftSurveyType: String,
    val leftElement: String,
    val leftSubElement: String?,
    val rightSurveyType: String,
    val rightElement: String,
    val rightSubElement: String?,
    val errorMessage: String
)
