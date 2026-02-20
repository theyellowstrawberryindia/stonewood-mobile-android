package uk.co.savills.stonewood.model.survey.element

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.HHSRSElementRating
import uk.co.savills.stonewood.model.survey.entry.HHSRSSurveyElementEntryModel

@SuppressLint("InvalidClassName")
data class HHSRSSurveyElementModel(
    val id: String,
    val sequenceNumber: Int,
    val title: String,
    val exclude: Boolean,
    private var _entry: HHSRSSurveyElementEntryModel? = null,
    var isSelected: Boolean = false
) {
    val isComplete
        get() = entry.isComplete

    var entry: HHSRSSurveyElementEntryModel
        get() = requireNotNull(_entry)
        set(value) {
            _entry = value
        }

    val isExtraInformationRequired
        get() = entry.rating != null && entry.rating != HHSRSElementRating.TYPICAL
}
