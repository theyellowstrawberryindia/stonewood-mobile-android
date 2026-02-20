package uk.co.savills.stonewood.model.survey.entry

import uk.co.savills.stonewood.model.survey.StockSurveyType

data class StockSurveyElementEntryModel(
    val elementId: Int,
    var communalPartNumber: Int = 0,
    val surveyType: StockSurveyType,
    val sequenceNumber: Int,
    val title: String,
    var subElementNumber: Int? = null,
    var subElement: String = "",
    var subElementUserEntry: String = "",
    var date: Date? = null,
    var description: String = "",
    var repair: Boolean? = null,
    var repairDescription: String = "",
    var repairSpotPrice: Int? = null,
    var lifeRenewalBand: Int? = null,
    var lifeRenewalUnits: Int? = null,
    var asBuilt: Boolean? = null,
    val imagePaths: MutableList<String> = mutableListOf(),
    var noAccessReason: String = "",
    var existingAgeBand: Int? = null,
    var isIndividual: Boolean? = null,
    var isCloned: Boolean? = null,
    var isComplete: Boolean,
    var details: SurveyElementEntryDetailsModel? = null,
) {
    val isUserEntryRequired: Boolean
        get() = subElement.contains("<numeric>", ignoreCase = true) ||
            subElement.contains("<positiveNumeric>", ignoreCase = true) ||
            subElement.contains("<decimal>", ignoreCase = true) ||
            subElement.contains("<positiveDecimal>", ignoreCase = true) ||
            subElement.contains("<text>", ignoreCase = true)

    var minimumPhotosRequired = 0
}
