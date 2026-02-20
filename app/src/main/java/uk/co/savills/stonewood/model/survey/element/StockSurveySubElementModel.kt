package uk.co.savills.stonewood.model.survey.element

data class StockSurveySubElementModel(
    val id: Int,
    val number: Int,
    val title: String,
    val skippedElements: List<String>,
    val life: Int,
    val minPhotoCount: Int,
    val cost: Cost,
    var isSkipped: Boolean = false
) {
    data class Cost(
        val house: Double,
        val bungalow: Double,
        val flat: Double,
        val block: Double
    )
}
