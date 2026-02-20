package uk.co.savills.stonewood.model.survey.element.energy

data class EnergySurveyElementGroupModel(
    val title: String,
    val sections: List<EnergySurveyElementSectionModel>,
    var isSelected: Boolean = false,
) {
    val isComplete: Boolean
        get() = sections.all { it.isComplete }

    val isSkipped: Boolean
        get() = sections.all { it.isSkipped }

    val isSpecial: Boolean
        get() = title.equals("measurements", true)
}
