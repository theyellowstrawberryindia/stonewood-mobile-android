package uk.co.savills.stonewood.model.survey.property

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.LocationModel
import uk.co.savills.stonewood.model.survey.element.RiskAssessmentSurveyElementModel
import uk.co.savills.stonewood.model.survey.noaccess.NoAccessEntryModel
import uk.co.savills.stonewood.model.survey.project.ProjectModel
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import java.time.Instant

@SuppressLint("InvalidMethodName")
data class PropertyModel(
    val id: Int,
    val order: Int,
    val UPRN: String,
    val TA: String,
    val strata: String,
    val address: AddressModel,
    val surveyTypeOriginal: PropertySurveyType,
    var surveyType: PropertySurveyType,
    val section: String,
    var contact: ContactModel,
    var frontDoorPhoto: String,
    var location: LocationModel,
    val surveyStatus: SurveyStatus = SurveyStatus(),
    val noAccessHistory: List<NoAccessEntryModel> = listOf(),
    val extBlockPhotos: MutableList<String> = mutableListOf(),
    var extPhotosClonedFrom: String = "",
    val hasExternalPhoto: Boolean = false,
    val isDeleted: Boolean = false,
    var createdAt: Instant?,
    var updatedAt: Instant?,
) {
    val isRequired = TA == "T" || TA == "PT"

    val status: PropertyStatus
        get() {
            if (surveyStatus.areRequiredSurveysComplete) return PropertyStatus.SURVEYED

            with(noAccessHistory) {
                if (any { it.reason.equals("void", ignoreCase = true) }) {
                    return PropertyStatus.VOID
                }

                if (
                    any {
                        it.reason.equals("tenant refusal", ignoreCase = true) ||
                            it.reason.equals("private", ignoreCase = true) ||
                            it.reason.equals(RiskAssessmentSurveyElementModel.NO_ACCESS_REASON, ignoreCase = true)
                    }
                ) return PropertyStatus.REFUSED_OR_PRIVATE

                val noAccessOrFailedCount = count {
                    it.reason.equals("no answer at door", ignoreCase = true) ||
                        it.reason.equals("failed appointment", ignoreCase = true)
                }

                if (noAccessOrFailedCount > 1) {
                    return PropertyStatus.REPEATED_NO_ACCESS_OR_FAILED
                } else if (noAccessOrFailedCount == 1) {
                    return PropertyStatus.NO_ACCESS_OR_FAILED
                }
            }

            return if (contact.isAvailable) {
                PropertyStatus.CONTACT_AVAILABLE
            } else {
                PropertyStatus.CONTACT_UNAVAILABLE
            }
        }

    val isFrontDoorPhotoRequired
        get() = listOf(PropertySurveyType.I, PropertySurveyType.ISAP, PropertySurveyType.IE, PropertySurveyType.IESAP).contains(surveyType)

    fun isEnabled(project: ProjectModel, survey: SurveyModel, surveys: List<SurveyModel>): Boolean {
        if (survey.type == SurveyType.VALIDATION) {
            return surveys.filterNot { it.type == SurveyType.VALIDATION }.all {
                when (it.type) {
                    SurveyType.RISK_ASSESSMENT -> surveyStatus.isRiskAssessmentSurveyComplete
                    SurveyType.QUALITY_STANDARD -> surveyStatus.isQualityStandardSurveyComplete
                    SurveyType.STOCK -> surveyStatus.isStockSurveyComplete
                    SurveyType.ENERGY -> surveyStatus.isEnergySurveyComplete
                    SurveyType.HHSRS -> surveyStatus.isHHSRSSurveyComplete
                    else -> false
                }
            }
        }

        if (
            (
                survey.type == SurveyType.RISK_ASSESSMENT ||
                    !surveys.any { it.type == SurveyType.RISK_ASSESSMENT }
                )
        ) {
            return true
        }

        var isEnabled = surveyStatus.isRiskAssessmentSurveyComplete

        if (isFrontDoorPhotoRequired) {
            isEnabled = isEnabled && frontDoorPhoto.isNotEmpty()
        }

        if (hasExternalPhoto) {
            isEnabled = isEnabled && extBlockPhotos.size >= project.numberOfSharedExternalPhotos
        }

        return isEnabled
    }

    fun toStatsModel(): PropertyStatsModel {
        return PropertyStatsModel(
            section,
            UPRN,
            strata,
            isRequired,
            surveyStatus.areRequiredSurveysComplete
        )
    }
}
