package uk.co.savills.stonewood.model.survey.entry

import android.annotation.SuppressLint
import java.time.Instant

@SuppressLint("InvalidMethodName")
data class SurveyElementEntryDetailsModel(
    val propertyUPRN: String,
    val entryInstant: Instant,
    val updateInstant: Instant
)
