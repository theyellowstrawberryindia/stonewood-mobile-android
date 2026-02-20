package uk.co.savills.stonewood.model.survey.element.validation

import uk.co.savills.stonewood.model.survey.element.StockSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.Validatable
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementModel
import uk.co.savills.stonewood.model.survey.project.SurveyType

data class ValidationOperand(
    val elementTitle: String,
    val subElements: List<String>,
    val surveyType: SurveyType,
    var element: Validatable? = null
) {
    val elementId: Int
        get() {
            return requireNotNull(
                if (surveyType == SurveyType.STOCK) {
                    (element as? StockSurveyElementModel)?.id
                } else {
                    (element as? EnergySurveyElementModel)?.id
                }
            )
        }

    val answer: String
        get() {
            return if (surveyType == SurveyType.STOCK) {
                val element = element as? StockSurveyElementModel ?: return ""
                element.entry.subElementUserEntry.ifBlank {
                    element.entry.subElement
                }
            } else {
                (element as? EnergySurveyElementModel)?.entry?.subElement.orEmpty()
            }
        }

    val subElement: String?
        get() {
            return if (surveyType == SurveyType.STOCK) {
                (element as? StockSurveyElementModel)?.entry?.subElement
            } else {
                (element as? EnergySurveyElementModel)?.entry?.subElement
            }
        }
}
