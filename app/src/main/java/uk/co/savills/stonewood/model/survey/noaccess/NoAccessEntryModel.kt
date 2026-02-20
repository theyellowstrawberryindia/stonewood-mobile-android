package uk.co.savills.stonewood.model.survey.noaccess

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.LocationModel
import java.time.Instant

@SuppressLint("InvalidMethodName")
data class NoAccessEntryModel(
    val UPRN: String,
    val reason: String,
    val remarks: String,
    val imagePaths: List<String>,
    val location: LocationModel,
    val createdAt: Instant,
)
