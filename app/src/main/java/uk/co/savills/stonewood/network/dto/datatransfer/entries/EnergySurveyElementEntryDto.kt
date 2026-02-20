package uk.co.savills.stonewood.network.dto.datatransfer.entries

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass

@SuppressLint("InvalidMethodName")
@JsonClass(generateAdapter = true)
data class EnergySurveyElementEntryDto(
    val questionId: Int,
    val question: String,
    val responseId: String,
    val response: String,
    val propertyAddressUPRN: String,
    val surveyorUserName: String,
    val createdAt: String,
    val updatedAt: String,
)
