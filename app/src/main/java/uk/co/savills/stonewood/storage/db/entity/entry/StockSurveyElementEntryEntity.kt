package uk.co.savills.stonewood.storage.db.entity.entry

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity(
    tableName = "stock_survey_element_result_table",
    primaryKeys = ["element_id", "communal_part_number", "project_id", "property_uprn"]
)
data class StockSurveyElementEntryEntity(
    @ColumnInfo(name = "element_id")
    val elementId: Int = 0,

    @ColumnInfo(name = "communal_part_number")
    val communalPartNumber: Int,

    @ColumnInfo(name = "survey_type")
    val surveyType: String,

    @ColumnInfo(name = "sequence_number")
    val sequenceNumber: Int,

    val title: String,

    @ColumnInfo(name = "sub_element_number")
    val subElementNumber: Int,

    @ColumnInfo(name = "sub_element")
    val subElement: String,

    @ColumnInfo(name = "sub_element_user_entry")
    val subElementUserEntry: String,

    val description: String,

    val repair: Boolean?,

    @ColumnInfo(name = "repair_description")
    val repairDescription: String,

    @ColumnInfo(name = "repair_spot_price")
    val repairSpotPrice: Int?,

    @ColumnInfo(name = "life_renewal_band")
    val lifeRenewalBand: Int?,

    @ColumnInfo(name = "life_renewal_units")
    val lifeRenewalUnits: Int?,

    @ColumnInfo(name = "as_built")
    val asBuilt: Boolean?,

    @ColumnInfo(name = "existing_age_band")
    val existingAgeBand: Int?,

    @ColumnInfo(name = "image_paths")
    val imagePaths: String,

    @ColumnInfo(name = "no_access_reason")
    val noAccessReason: String,

    @ColumnInfo(name = "is_individual")
    val isIndividual: Boolean?,

    @ColumnInfo(name = "is_cloned")
    val isCloned: Boolean?,

    @ColumnInfo(name = "is_complete")
    val isComplete: Boolean,

    @Embedded
    override val details: SurveyElementEntryDetails
) : SurveyElementEntryEntity
