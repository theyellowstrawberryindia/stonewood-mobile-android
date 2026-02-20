package uk.co.savills.stonewood.model.survey.element

import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer

interface CloseEndedQuestionElementModel {
    val question: String
    val answer: CloseEndedQuestionAnswer
}
