package uk.co.savills.stonewood.network.dto

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass

@SuppressLint("InvalidClassName")
@JsonClass(generateAdapter = true)
data class HHSRSSevereIssueDto(
    val surveyorId: Int,
    val propertyAddressId: Int,
    val itemName: String,
    val attachments: List<EmailAttachmentDto>,
    val remark: String,
    val internalLocations: List<String>,
    val externalLocations: List<String>
)
