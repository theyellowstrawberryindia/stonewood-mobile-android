package uk.co.savills.stonewood.model.survey.entry

data class EnergySurveyElementEntryModel(
    val elementId: Int,
    val element: String,
    var subElementId: String = "",
    var subElement: String = "",
    var details: SurveyElementEntryDetailsModel? = null
)
