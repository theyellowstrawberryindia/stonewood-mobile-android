package uk.co.savills.stonewood.model.survey.element

import uk.co.savills.stonewood.model.survey.BandModel
import uk.co.savills.stonewood.model.survey.StockSurveyType
import uk.co.savills.stonewood.model.survey.entry.StockSurveyElementEntryModel

data class StockSurveyElementModel(
    val id: Int,
    val sequenceNumber: Int,
    val title: String,
    var group: String,
    val surveyType: StockSurveyType,
    val unit: UnitType,
    val unitBlock: UnitType,
    val isCommunal: Boolean = false,
    val warnValueLow: Int = Int.MIN_VALUE,
    val warnValueHigh: Int = Int.MAX_VALUE,
    val useQuantityAdder: Boolean = false,
    val useQuantityMultiplier: Boolean = false,
    val disableAgeBandFiltering: Boolean = false,
    val isAsBuiltRequired: Boolean = false,
    var subElements: List<StockSurveySubElementModel>,
    private var elementEntry: StockSurveyElementEntryModel? = null
) : Validatable {
    var entry: StockSurveyElementEntryModel
        get() = requireNotNull(elementEntry)
        set(value) {
            elementEntry = value
        }

    var unitTobeUsed = unit

    val isSkipped
        get() = subElements.all { it.isSkipped }

    val isExtraInfoRequired: Boolean
        get() {
            return unitTobeUsed != UnitType.IO &&
                !isSpecialEntry &&
                entry.subElement.isNotEmpty() &&
                !entry.subElement.contains("<photo>", ignoreCase = true) &&
                !isDateEntry
        }

    val isSpecialEntry: Boolean
        get() {
            return entry.subElement.equals("None", ignoreCase = true) ||
                entry.subElement.equals("Not Present", ignoreCase = true) ||
                entry.subElement.equals("Not applicable", ignoreCase = true) ||
                entry.subElement.equals("N.A.", ignoreCase = true)
        }

    val isRenewalQuantityRequired: Boolean
        get() {
            return (
                unitTobeUsed == UnitType.NO ||
                    unitTobeUsed == UnitType.LM ||
                    unitTobeUsed == UnitType.M2
                ) &&
                !isRebuildsEntry
        }

    val isAdditionalRenewalQuantityRequired: Boolean
        get() = surveyType != StockSurveyType.INTERNAL && useQuantityAdder

    val isDateEntry: Boolean
        get() = entry.subElement.contains("(?i)(?:<date>|<date past>|<date future>)".toRegex())

    val isPastDateEntry: Boolean
        get() = entry.subElement.contains("<date past>")

    val isFutureDateEntry: Boolean
        get() = entry.subElement.contains("<date future>")

    val isPhotoRequired: Boolean
        get() {
            return !isSpecialEntry && (
                entry.subElement.contains("<photo>", ignoreCase = true) ||
                    entry.minimumPhotosRequired > 0
                )
        }

    val isRebuildsEntry: Boolean
        get() = entry.subElement.equals("rebuilds", ignoreCase = true)

    fun setAsSkipped() {
        entry = StockSurveyElementEntryModel(
            id,
            surveyType = surveyType,
            sequenceNumber = sequenceNumber,
            title = title,
            communalPartNumber = if (surveyType == StockSurveyType.COMMUNAL) 1 else 0,
            isComplete = false,
            subElement = "svs_skipped",
            subElementNumber = 9999
        )
    }

    fun getHighlightedRenewalBands(renewalBands: List<BandModel>): List<BandModel> {
        val life =
            subElements.find { it.title == entry.subElement }?.life

        if (
            life == null ||
            entry.existingAgeBand == null ||
            surveyType != StockSurveyType.INTERNAL
        ) {
            return listOf()
        }

        val value = life - requireNotNull(entry.existingAgeBand)

        return renewalBands.filter { band ->
            when {
                value < 0 -> {
                    band.upperBound != null && band.lowerBound > 0 && band.upperBound <= 5
                }
                band.upperBound != null -> {
                    value in band.lowerBound..band.upperBound
                }
                else -> {
                    value > band.lowerBound
                }
            }
        }
    }

    fun isRenewalBandExtreme(renewalBands: List<BandModel>): Boolean {
        val band = renewalBands.find { it.lowerBound == entry.lifeRenewalBand } ?: return false

        if (band.upperBound == null || band.upperBound > 5) return false

        val highlightedBands = getHighlightedRenewalBands(renewalBands)
        val bandIndex = renewalBands.indexOf(band)

        return highlightedBands.isNotEmpty() &&
            highlightedBands.all { renewalBands.indexOf(it) - bandIndex >= 2 }
    }

    fun resetEntry() {
        entry = StockSurveyElementEntryModel(
            id,
            surveyType = surveyType,
            sequenceNumber = sequenceNumber,
            title = title,
            communalPartNumber = entry.communalPartNumber,
            isComplete = false
        )
    }

    enum class UnitType {
        IO, PP, CV, NO, LM, M2;

        companion object {
            fun from(text: String): UnitType {
                return requireNotNull(values().find { it.name.equals(text, ignoreCase = true) })
            }
        }
    }

    override val isComplete: Boolean
        get() = entry.isComplete
}
