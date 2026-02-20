package uk.co.savills.stonewood.screen.survey.survey.validation

import uk.co.savills.stonewood.model.survey.element.validation.ValidationCategory
import uk.co.savills.stonewood.model.survey.element.validation.ValidationElementModel

data class CategoryPresentationModel(
    val category: ValidationCategory,
    val groups: Map<String, List<ValidationElementModel>>
) {
    var isSelected = false
}
