package uk.co.savills.stonewood.storage.db.entity.element

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "stock_survey_sub_element_table",
    primaryKeys = ["id", "element_id", "project_id"]
)
data class StockSurveySubElementEntity(
    val id: Int,

    @ColumnInfo(name = "project_id")
    val projectId: String,

    @ColumnInfo(name = "number")
    val number: Int,

    @ColumnInfo(name = "element_id")
    val elementId: Int,

    val title: String,

    @ColumnInfo(name = "skipped_elements")
    val skippedElements: String,

    val life: Int,

    @ColumnInfo(name = "min_photo_count")
    val minPhotoCount: Int,

    @ColumnInfo(name = "cost_house")
    val costHouse: Double,

    @ColumnInfo(name = "cost_bungalow")
    val costBungalow: Double,

    @ColumnInfo(name = "cost_flat")
    val costFlat: Double,

    @ColumnInfo(name = "cost_block")
    val costBlock: Double
)
