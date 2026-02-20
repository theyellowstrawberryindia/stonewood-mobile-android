package uk.co.savills.stonewood.model.survey.element.energy

data class EnergySurveyElementSectionModel(
    val title: String,
    val elements: List<EnergySurveyElementModel>
) {
    val isComplete: Boolean
        get() = elements.filterNot { it.isSkipped }.all { it.isComplete }

    val isSkipped: Boolean
        get() = elements.all { it.isSkipped }
}
