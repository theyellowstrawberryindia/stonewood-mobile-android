package uk.co.savills.stonewood.model.survey.element

data class StockSurveyElementGroupModel(
    val title: String,
    val elements: List<StockSurveyElementModel>,
    var isSelected: Boolean = false,
) {
    val isComplete: Boolean
        get() = elements.filterNot { it.isSkipped }.all { it.entry.isComplete }
}
