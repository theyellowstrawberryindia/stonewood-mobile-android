package uk.co.savills.stonewood.model.survey.entry

import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer

data class QualityStandardSurveyElementEntryModel(
    val elementId: String,
    val question: String,
    var answer: CloseEndedQuestionAnswer,
    override var details: SurveyElementEntryDetailsModel? = null,
) : SurveyElementEntryModel
