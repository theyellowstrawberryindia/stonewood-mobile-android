package uk.co.savills.stonewood.model.survey.element

import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer
import uk.co.savills.stonewood.model.survey.entry.QualityStandardSurveyElementEntryModel

data class QualityStandardSurveyElementModel(
    val id: String,
    val sequenceNumber: Int,
    override val question: String,
    val exclude: Boolean,
    private var _entry: QualityStandardSurveyElementEntryModel? = null
) : CloseEndedQuestionElementModel {
    var entry: QualityStandardSurveyElementEntryModel
        get() = requireNotNull(_entry)
        set(value) {
            _entry = value
        }

    override val answer: CloseEndedQuestionAnswer
        get() = entry.answer
}
