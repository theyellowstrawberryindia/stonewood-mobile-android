package uk.co.savills.stonewood.storage.db.entity.element

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "stock_survey_element_table",
    primaryKeys = ["id", "project_id"]
)
data class StockSurveyElementEntity(
    val id: Int,

    @ColumnInfo(name = "project_id")
    val projectId: String,

    @ColumnInfo(name = "sequence_number")
    val sequenceNumber: Int,

    val title: String,

    val group: String,

    @ColumnInfo(name = "survey_type")
    val surveyType: String,

    val unit: String,

    @ColumnInfo(name = "unit_block")
    val unitBlock: String,

    @ColumnInfo(name = "is_communal")
    val isCommunal: Boolean,

    @ColumnInfo(name = "warn_value")
    val warnValue: Int,

    @ColumnInfo(name = "warn_value_low")
    val warnValueLow: Int,

    @ColumnInfo(name = "use_quantity_adder")
    val useQuantityAdder: Boolean,

    @ColumnInfo(name = "use_quantity_multiplier")
    val useQuantityMultiplier: Boolean,

    @ColumnInfo(name = "disable_age_band_filtering")
    val disableAgeBandFiltering: Boolean,

    @ColumnInfo(name = "is_as_built_required")
    val isAsBuiltRequired: Boolean
)
