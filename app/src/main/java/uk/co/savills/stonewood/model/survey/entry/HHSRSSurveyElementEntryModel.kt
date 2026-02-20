package uk.co.savills.stonewood.model.survey.entry

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.HHSRSElementRating

@SuppressLint("InvalidClassName")
data class HHSRSSurveyElementEntryModel(
    val id: String,
    val name: String,
    var rating: HHSRSElementRating? = null,
    var ratingCost: String = "",
    var ratingDescription: String = "",
    val imagePaths: MutableList<String> = mutableListOf(),
    var internalLocations: List<String> = listOf(),
    var externalLocations: List<String> = listOf(),
    var changedToTypicalFrom: HHSRSElementRating? = null,
    var isComplete: Boolean,
    override var details: SurveyElementEntryDetailsModel? = null,
) : SurveyElementEntryModel
