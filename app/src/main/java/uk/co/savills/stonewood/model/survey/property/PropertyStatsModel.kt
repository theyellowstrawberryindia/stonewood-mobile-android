package uk.co.savills.stonewood.model.survey.property

data class PropertyStatsModel(
    val section: String,
    val uprn: String,
    val strata: String,
    val isRequired: Boolean,
    val isComplete: Boolean,
)
