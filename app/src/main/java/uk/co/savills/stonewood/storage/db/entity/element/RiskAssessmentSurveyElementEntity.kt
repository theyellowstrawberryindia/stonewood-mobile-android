package uk.co.savills.stonewood.storage.db.entity.element

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "risk_assessment_survey_element_table",
    primaryKeys = ["id", "project_id"]
)
data class RiskAssessmentSurveyElementEntity(
    val id: String,

    @ColumnInfo(name = "sequence_number")
    val sequenceNumber: Int,

    val question: String,

    val type: Int,

    val exclude: Boolean,

    @ColumnInfo(name = "project_id")
    val projectId: String,
)
