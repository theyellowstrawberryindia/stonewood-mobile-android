package uk.co.savills.stonewood.model.survey.entry

import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer

class RiskAssessmentSurveyElementEntryModel(
    val elementId: String,
    val question: String,
    var answer: CloseEndedQuestionAnswer,
    override var details: SurveyElementEntryDetailsModel? = null,
) : SurveyElementEntryModel
