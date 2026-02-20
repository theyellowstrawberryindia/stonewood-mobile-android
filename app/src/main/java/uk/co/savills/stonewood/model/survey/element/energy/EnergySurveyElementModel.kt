package uk.co.savills.stonewood.model.survey.element.energy

import uk.co.savills.stonewood.model.survey.element.Validatable
import uk.co.savills.stonewood.model.survey.entry.EnergySurveyElementEntryModel

data class EnergySurveyElementModel(
    val id: Int,
    val serialNumber: Int,
    val group: String,
    val section: String,
    val subSection: String,
    val titleShort: String,
    val titleLong: String,
    val warnValueLow: Int,
    val warnValueHigh: Int,
    val limitValue: Int,
    val type: EnergySurveyElementType,
    val subElements: MutableList<EnergySurveySubElementModel> = mutableListOf()
) : Validatable {
    lateinit var entry: EnergySurveyElementEntryModel

    override val isComplete: Boolean
        get() = entry.subElement.isNotBlank()

    private var _isSkipped = false
    var isSkipped: Boolean
        get() = _isSkipped || subElements.all { it.isSkipped }
        set(value) {
            _isSkipped = value
        }

    val isPreSelection
        get() = type == EnergySurveyElementType.DROP_DOWN && subElements.filterNot { it.isSkipped }.size == 1

    fun resetEntry() {
        entry.subElementId = ""
        entry.subElement = ""
    }
}
