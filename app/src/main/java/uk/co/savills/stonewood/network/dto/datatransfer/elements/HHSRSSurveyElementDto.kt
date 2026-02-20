package uk.co.savills.stonewood.network.dto.datatransfer.elements

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass

@SuppressLint("InvalidClassName")
@JsonClass(generateAdapter = true)
data class HHSRSSurveyElementDto(
    val id: Int,
    val elementId: String,
    val elementName: String,
    val elementExclude: Boolean,
)
