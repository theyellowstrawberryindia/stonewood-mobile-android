package uk.co.savills.stonewood.storage.db.entity.element

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "validation_elements",
    primaryKeys = ["id", "project_id"]
)
data class ValidationElementEntity(

    val id: Int,

    @ColumnInfo(name = "project_id")
    val projectId: String,

    val operator: String,

    val group: String,

    val category: Int,

    @ColumnInfo(name = "left_survey_type")
    val leftSurveyType: Int,

    @ColumnInfo(name = "left_element")
    val leftElement: String,

    @ColumnInfo(name = "left_sub_element")
    val leftSubElement: String,

    @ColumnInfo(name = "right_survey_type")
    val rightSurveyType: Int,

    @ColumnInfo(name = "right_element")
    val rightElement: String,

    @ColumnInfo(name = "right_sub_element")
    val rightSubElement: String,

    @ColumnInfo(name = "error_message")
    val errorMessage: String
)
