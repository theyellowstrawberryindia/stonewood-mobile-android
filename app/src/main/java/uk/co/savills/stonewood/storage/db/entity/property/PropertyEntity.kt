package uk.co.savills.stonewood.storage.db.entity.property

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@SuppressLint("InvalidMethodName")
@Entity(tableName = "property_table")
data class PropertyEntity(
    @PrimaryKey
    val key: String,

    var order: Int,

    val id: Int,

    @ColumnInfo(name = "project_Id")
    val projectId: String,

    @ColumnInfo(name = "uprn")
    val UPRN: String,

    @ColumnInfo(name = "t_or_a")
    var TA: String,

    var strata: String,

    var number: String,

    var address1: String,

    var address2: String,

    var address3: String,

    var address4: String,

    @ColumnInfo(name = "postal_code")
    var postalCode: String,

    @ColumnInfo(name = "survey_type1")
    val surveyType1: String,

    @ColumnInfo(name = "survey_type2")
    val surveyType2: String,

    val section: String,

    @ColumnInfo(name = "contact_number1")
    var contactNumber1: String,

    @ColumnInfo(name = "contact_number2")
    var contactNumber2: String,

    @ColumnInfo(name = "contact_notes")
    var contactNotes: String,

    @ColumnInfo(name = "front_door_photo")
    val frontDoorPhoto: String,

    @ColumnInfo(name = "energy_ext_photos")
    val extPhotos: String,

    @ColumnInfo(name = "energy_ext_photos_cloned_from")
    val extPhotosClonedFrom: String,

    @ColumnInfo(name = "has_external_photo")
    var hasExternalPhoto: Boolean,

    val latitude: Double,

    val longitude: Double,

    @ColumnInfo(name = "is_risk_assessment_survey_complete")
    val isRiskAssessmentSurveyComplete: Boolean,

    @ColumnInfo(name = "is_quality_standard_survey_complete")
    val isQualityStandardSurveyComplete: Boolean,

    @ColumnInfo(name = "is_stock_survey_complete")
    val isStockSurveyComplete: Boolean,

    @ColumnInfo(name = "is_energy_survey_complete")
    val isEnergySurveyComplete: Boolean,

    @ColumnInfo(name = "is_hhsrs_survey_complete")
    val isHHSRSSurveyComplete: Boolean,

    @ColumnInfo(name = "is_validation_complete")
    val isValidationComplete: Boolean,

    @ColumnInfo(name = "are_required_surveys_complete")
    val areRequiredSurveysComplete: Boolean,

    @ColumnInfo(name = "is_deleted")
    var isDeleted: Boolean,

    @ColumnInfo(name = "created_At")
    val createdAt: String?,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,
)
