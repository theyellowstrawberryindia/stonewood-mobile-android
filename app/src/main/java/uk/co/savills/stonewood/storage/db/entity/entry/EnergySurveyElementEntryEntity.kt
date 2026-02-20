package uk.co.savills.stonewood.storage.db.entity.entry

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity(
    tableName = "energy_survey_element_result_table",
    primaryKeys = ["element_id", "project_id", "property_uprn"]
)
data class EnergySurveyElementEntryEntity(
    @ColumnInfo(name = "element_id")
    val elementId: Int,

    val element: String,

    @ColumnInfo(name = "sub_element_id")
    val subElementId: String,

    @ColumnInfo(name = "sub_element")
    val subElement: String,

    @Embedded
    override val details: SurveyElementEntryDetails,
) : SurveyElementEntryEntity
