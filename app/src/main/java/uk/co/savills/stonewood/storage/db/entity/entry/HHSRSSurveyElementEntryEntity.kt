package uk.co.savills.stonewood.storage.db.entity.entry

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@SuppressLint("InvalidClassName")
@Entity(
    tableName = "hhsrs_survey_element_result_table",
    primaryKeys = ["element_id", "project_id", "property_uprn"]
)
data class HHSRSSurveyElementEntryEntity(
    @ColumnInfo(name = "element_id")
    val elementId: String,

    val name: String,

    val rating: String,

    @ColumnInfo(name = "rating_description")
    val ratingDescription: String,

    @ColumnInfo(name = "rating_cost")
    val ratingCost: String,

    @ColumnInfo(name = "image_paths")
    val imagePaths: String,

    @ColumnInfo(name = "internal_locations")
    val internalLocations: String,

    @ColumnInfo(name = "external_locations")
    val externalLocations: String,

    @ColumnInfo(name = "changed_to_typical_from")
    val changedToTypicalFrom: String?,

    @ColumnInfo(name = "is_complete")
    val isComplete: Boolean,

    @Embedded
    override val details: SurveyElementEntryDetails,
) : SurveyElementEntryEntity
