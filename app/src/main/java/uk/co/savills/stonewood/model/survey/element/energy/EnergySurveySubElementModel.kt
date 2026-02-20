package uk.co.savills.stonewood.model.survey.element.energy

data class EnergySurveySubElementModel(
    val id: String,
    val serialNumber: Int,
    val title: String,
    val description: String,
    val skipCodes: List<String>,
    val isRare: Boolean
) {
    var isSkipped: Boolean = false
}
