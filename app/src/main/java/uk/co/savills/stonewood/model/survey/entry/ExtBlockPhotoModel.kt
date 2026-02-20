package uk.co.savills.stonewood.model.survey.entry

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.property.AddressModel
import java.time.Instant

@SuppressLint("InvalidMethodName")
data class ExtBlockPhotoModel(
    var id: Int?,
    val propertyUPRN: String,
    val address: AddressModel,
    val surveyor: String,
    val imagePaths: MutableList<String>,
    val syncId: Int?,
    val createdAt: Instant
)
