package uk.co.savills.stonewood.storage.db.entity.entry

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity(
    tableName = "risk_assessment_survey_element_result_table",
    primaryKeys = ["element_id", "project_id", "property_uprn"]
)
data class RiskAssessmentSurveyElementEntryEntity(
    @ColumnInfo(name = "element_id")
    val elementId: String,

    val question: String,

    val answer: String,

    @Embedded
    override val details: SurveyElementEntryDetails,
) : SurveyElementEntryEntity
