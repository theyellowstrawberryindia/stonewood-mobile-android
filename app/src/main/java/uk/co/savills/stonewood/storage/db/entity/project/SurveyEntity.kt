package uk.co.savills.stonewood.storage.db.entity.project

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "survey_table",
    primaryKeys = ["project_id", "type"]
)
data class SurveyEntity(
    @ColumnInfo(name = "project_id")
    val projectId: String,

    val type: Int,

    val title: String?
)
