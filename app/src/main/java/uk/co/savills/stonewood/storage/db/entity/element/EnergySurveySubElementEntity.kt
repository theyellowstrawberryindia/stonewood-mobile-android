package uk.co.savills.stonewood.storage.db.entity.element

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "energy_survey_sub_element_table",
    primaryKeys = ["id", "element_id", "project_id"]
)
data class EnergySurveySubElementEntity(
    val id: String,

    @ColumnInfo(name = "element_id")
    val elementId: Int,

    @ColumnInfo(name = "project_id")
    val projectId: String,

    @ColumnInfo(name = "serial_number")
    val serialNumber: Int,

    val title: String,

    val description: String,

    @ColumnInfo(name = "skip_codes")
    val skipCodes: String,

    @ColumnInfo(name = "is_rare")
    val isRare: Boolean
)
