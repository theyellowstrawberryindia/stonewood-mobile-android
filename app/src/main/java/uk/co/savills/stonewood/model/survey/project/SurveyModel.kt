package uk.co.savills.stonewood.model.survey.project

import android.content.Context
import uk.co.savills.stonewood.R

data class SurveyModel(
    val type: SurveyType,
    val title: String?,
    var isComplete: Boolean = false,
) {
    fun getTitle(context: Context): String {
        if (!title.isNullOrBlank()) return title

        return context.getString(
            when (type) {
                SurveyType.RISK_ASSESSMENT -> R.string.risk_assessment_survey_default_name
                SurveyType.HHSRS -> R.string.hhsrs_survey_default_name
                SurveyType.ENERGY -> R.string.energy_survey_default_name
                SurveyType.STOCK -> R.string.stock_survey_default_name
                SurveyType.QUALITY_STANDARD -> R.string.quality_standard_survey_default_name
                SurveyType.VALIDATION -> R.string.validation_label
            }
        )
    }
}
