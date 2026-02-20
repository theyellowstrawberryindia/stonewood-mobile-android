package uk.co.savills.stonewood.screen.survey.survey

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.screen.survey.survey.energy.EnergySurveyFragment
import uk.co.savills.stonewood.screen.survey.survey.hhsrs.HHSRSSurveyFragment
import uk.co.savills.stonewood.screen.survey.survey.qualitystandard.QualityStandardSurveyFragment
import uk.co.savills.stonewood.screen.survey.survey.riskassessment.RiskAssessmentSurveyFragment
import uk.co.savills.stonewood.screen.survey.survey.stocksurvey.StockSurveyFragment
import uk.co.savills.stonewood.screen.survey.survey.validation.ValidationFragment

class SurveyAdapter(
    fragment: Fragment,
    private val surveys: List<SurveyModel>,
) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (surveys[position].type) {
            SurveyType.RISK_ASSESSMENT -> RiskAssessmentSurveyFragment()
            SurveyType.QUALITY_STANDARD -> QualityStandardSurveyFragment()
            SurveyType.STOCK -> StockSurveyFragment()
            SurveyType.ENERGY -> EnergySurveyFragment()
            SurveyType.HHSRS -> HHSRSSurveyFragment()
            SurveyType.VALIDATION -> ValidationFragment()
        }
    }

    override fun getItemCount() = surveys.size
}
