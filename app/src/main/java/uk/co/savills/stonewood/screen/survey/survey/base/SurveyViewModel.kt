package uk.co.savills.stonewood.screen.survey.survey.base

import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.util.SingleLiveEvent

interface SurveyViewModel {
    val surveyUpdated: SingleLiveEvent<Nothing?>
    val elementFind: SingleLiveEvent<Pair<SurveyType, Int>>
}
