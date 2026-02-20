package uk.co.savills.stonewood.model.survey.element

import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer
import uk.co.savills.stonewood.model.survey.entry.RiskAssessmentSurveyElementEntryModel

data class RiskAssessmentSurveyElementModel(
    val id: String,
    val sequenceNumber: Int,
    override val question: String,
    val type: RiskAssessmentElementType,
    val exclude: Boolean,
    private var _entry: RiskAssessmentSurveyElementEntryModel? = null
) : CloseEndedQuestionElementModel {
    var entry: RiskAssessmentSurveyElementEntryModel
        get() = requireNotNull(_entry)
        set(value) {
            _entry = value
        }

    override val answer: CloseEndedQuestionAnswer
        get() = entry.answer

    val isAnswerValid: Boolean
        get() {
            return when (type) {
                RiskAssessmentElementType.EXPECTED_YES -> answer == CloseEndedQuestionAnswer.YES
                RiskAssessmentElementType.EXPECTED_NO -> answer == CloseEndedQuestionAnswer.NO
                RiskAssessmentElementType.REQUIRED -> answer != CloseEndedQuestionAnswer.UNANSWERED
            }
        }

    companion object {
        const val NO_ACCESS_REASON = "COVID failure"
    }
}
