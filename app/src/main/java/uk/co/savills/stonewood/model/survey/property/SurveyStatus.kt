package uk.co.savills.stonewood.model.survey.property

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.model.survey.project.SurveyType

@SuppressLint("InvalidMethodName")
data class SurveyStatus(
    var isRiskAssessmentSurveyComplete: Boolean = false,
    var isQualityStandardSurveyComplete: Boolean = false,
    var isStockSurveyComplete: Boolean = false,
    var isEnergySurveyComplete: Boolean = false,
    var isHHSRSSurveyComplete: Boolean = false,
    var isValidationComplete: Boolean = false,
    var areRequiredSurveysComplete: Boolean = false
) {
    fun setCompletionStatus(type: SurveyType, isComplete: Boolean) {
        when (type) {
            SurveyType.RISK_ASSESSMENT -> isRiskAssessmentSurveyComplete = isComplete
            SurveyType.QUALITY_STANDARD -> isQualityStandardSurveyComplete = isComplete
            SurveyType.STOCK -> isStockSurveyComplete = isComplete
            SurveyType.ENERGY -> isEnergySurveyComplete = isComplete
            SurveyType.HHSRS -> isHHSRSSurveyComplete = isComplete
            SurveyType.VALIDATION -> isValidationComplete = isComplete
        }
    }

    fun evaluateRequiredSurveysCompletionStatus(requiredSurveys: List<SurveyModel>) {
        areRequiredSurveysComplete = requiredSurveys.all {
            when (it.type) {
                SurveyType.RISK_ASSESSMENT -> isRiskAssessmentSurveyComplete
                SurveyType.QUALITY_STANDARD -> isQualityStandardSurveyComplete
                SurveyType.STOCK -> isStockSurveyComplete
                SurveyType.ENERGY -> isEnergySurveyComplete
                SurveyType.HHSRS -> isHHSRSSurveyComplete
                SurveyType.VALIDATION -> isValidationComplete
            }
        }
    }
}
