package uk.co.savills.stonewood.storage.db.entity.element

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "energy_survey_element_table",
    primaryKeys = ["id", "project_id"]
)
data class EnergySurveyElementEntity(
    val id: Int,

    @ColumnInfo(name = "project_id")
    val projectId: String,

    @ColumnInfo(name = "serial_number")
    val serialNumber: Int,

    val group: String,

    val section: String,

    @ColumnInfo(name = "sub_section")
    val subSection: String,

    @ColumnInfo(name = "title_short")
    val titleShort: String,

    @ColumnInfo(name = "title_long")
    val titleLong: String,

    @ColumnInfo(name = "warn_value")
    val warnValue: Int,

    @ColumnInfo(name = "warn_value_low")
    val warnValueLow: Int,

    @ColumnInfo(name = "limit_value")
    val limitValue: Int,

    val type: String,
)
