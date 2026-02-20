package uk.co.savills.stonewood.storage.db.entity.entry

import android.annotation.SuppressLint
import androidx.room.ColumnInfo

@SuppressLint("InvalidMethodName")
data class SurveyElementEntryDetails(
    @ColumnInfo(name = "project_id")
    val projectId: String,

    @ColumnInfo(name = "property_uprn")
    val propertyUPRN: String,

    @ColumnInfo(name = "entry_timestamp")
    val entryTimestamp: String,

    @ColumnInfo(name = "update_timestamp")
    val updateTimestamp: String
)
