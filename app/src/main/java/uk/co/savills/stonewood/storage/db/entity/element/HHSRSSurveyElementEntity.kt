package uk.co.savills.stonewood.storage.db.entity.element

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity

@SuppressLint("InvalidClassName")
@Entity(
    tableName = "hhsrs_survey_element_table",
    primaryKeys = ["id", "project_id"]
)
data class HHSRSSurveyElementEntity(
    val id: String,

    @ColumnInfo(name = "sequence_number")
    val sequenceNumber: Int,

    val title: String,

    val exclude: Boolean,

    @ColumnInfo(name = "project_id")
    val projectId: String,
)
