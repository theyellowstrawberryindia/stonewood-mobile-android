package uk.co.savills.stonewood.screen.survey.survey.energy

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementGroupModel
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementModel
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementSectionModel
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementType
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveySubElementModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyViewModelBase
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.getNonNullValue
import uk.co.savills.stonewood.util.notify
import java.text.DecimalFormat

class EnergySurveyViewModel(
    application: Application,
    locationTracker: LocationTracker
) : SurveyViewModelBase(application, locationTracker), ElementUpdateListener {

    private val energySurveyElementRepository
        get() = appContainer.energySurveyElementRepository

    private val _groups = MutableLiveData<List<EnergySurveyElementGroupModel>>()
    val groups: LiveData<List<EnergySurveyElementGroupModel>>
        get() = _groups

    private val _selectedGroup = MutableLiveData<EnergySurveyElementGroupModel>()
    val selectedGroup: LiveData<EnergySurveyElementGroupModel>
        get() = _selectedGroup

    private val elements = mutableListOf<EnergySurveyElementModel>()

    val excessiveValueDetected = SingleLiveEvent<Pair<String, String>>()
    val excessivelyLowValueDetected = SingleLiveEvent<Pair<String, String>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            elements.addAll(energySurveyElementRepository.getElements())
        }.invokeOnCompletion {
            setupSkippedSubElements()
            setupGroups(elements)
        }
    }

    private fun setupSkippedSubElements() {
        val skipCodes = mutableListOf<String>()

        elements.forEach { element ->
            element.isSkipped = false
            if (element.type == EnergySurveyElementType.DROP_DOWN) {
                var subElement: EnergySurveySubElementModel? = null

                element.subElements.forEach {
                    it.isSkipped = false

                    if (it.id == element.entry.subElementId) {
                        subElement = it
                    }
                }

                if (subElement != null) {
                    val codes = requireNotNull(subElement).skipCodes.filterNot {
                        it.equals("default value", ignoreCase = true)
                    }
                    skipCodes.addAll(codes)
                }
            }
        }

        for (skipCode in skipCodes) {
            val isExcluding = skipCode.startsWith('!')

            val elementId = if (isExcluding) {
                skipCode.substringAfter('!').substringBefore('(')
            } else {
                skipCode.substringBefore('(')
            }.trim().toInt()

            val element = elements.find { it.id == elementId } ?: continue

            val value = skipCode.substringAfter('(').substringBefore(')').trim()

            when {
                isExcluding -> {
                    val skippedSubElementIds = value.split(",").map { it.trim() }
                    element.subElements.forEach {
                        if (skippedSubElementIds.contains(it.id)) {
                            it.isSkipped = true
                        }
                    }
                }

                value == "*" -> element.isSkipped = true

                else -> {
                    val includedSubElementIds = value.split(",")

                    element.subElements.forEach {
                        it.isSkipped = !includedSubElementIds.contains(it.id)
                    }
                }
            }
        }
    }

    private fun setupGroups(elements: List<EnergySurveyElementModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            val groups = elements
                .groupBy { it.group }
                .map { mapEntry ->
                    val sections = mapEntry.value.groupBy { it.section }.map {
                        EnergySurveyElementSectionModel(it.key, it.value)
                    }

                    EnergySurveyElementGroupModel(mapEntry.key, sections)
                }

            if (groups.isNotEmpty()) setSelectedGroup(groups[0])

            _groups.postValue(groups)
        }
    }

    private fun setSelectedGroup(group: EnergySurveyElementGroupModel) {
        group.isSelected = true
        _selectedGroup.postValue(group)
    }

    fun onGroupSelected(selectedGroup: EnergySurveyElementGroupModel) {
        _groups.value = groups.getNonNullValue().map { group ->
            val isSelected = group.title == selectedGroup.title

            group.copy(isSelected = isSelected).also {
                if (isSelected) setSelectedGroup(it)
            }
        }
    }

    override fun onPlaceHolderSelected(element: EnergySurveyElementModel) {
        if (element.entry.subElementId == "") return

        element.entry.subElementId = ""
        element.entry.subElement = ""

        setupSkippedSubElements()

        _selectedGroup.notify()
        _groups.notify()
    }

    override fun onSubElementSelected(
        subElement: EnergySurveySubElementModel,
        element: EnergySurveyElementModel
    ) {
        if (element.entry.subElementId == subElement.id) return

        if (element.entry.subElementId.isNotEmpty()) {
            val previouslySelectedSubElement =
                element.subElements.find { it.id == element.entry.subElementId }

            if (previouslySelectedSubElement != null) {
                val skipCodes = previouslySelectedSubElement.skipCodes
                resetAffectedElements(skipCodes)
            }
        }

        resetAffectedElements(subElement.skipCodes)

        element.entry.subElementId = subElement.id
        element.entry.subElement = subElement.title

        setupSkippedSubElements()

        _selectedGroup.notify()
        onEntryUpdate(element)
    }

    private fun resetAffectedElements(skipCodes: List<String>) {
        for (skipCode in skipCodes) {
            if (skipCode.equals("default value", true)) continue

            val isExcluding = skipCode.startsWith('!')

            val elementId = if (isExcluding) {
                skipCode.substringAfter('!').substringBefore('(')
            } else {
                skipCode.substringBefore('(')
            }.trim().toInt()

            val affectedElement = elements.find { it.id == elementId } ?: continue
            affectedElement.resetEntry()
            onEntryUpdate(affectedElement)

            if (affectedElement.entry.subElementId.isNotEmpty()) {
                val subElement =
                    affectedElement.subElements.find { it.id == affectedElement.entry.subElementId }
                resetAffectedElements(subElement?.skipCodes.orEmpty())
            }
        }
    }

    override fun onUserEntryUpdate(entry: String, element: EnergySurveyElementModel) {
        if (
            element.type == EnergySurveyElementType.QUANTITY ||
            element.type == EnergySurveyElementType.QUANTITY0
        ) {
            val value = try {
                entry.toInt()
            } catch (e: Exception) {
                null
            }

            element.entry.subElement = if (value != null && value > element.limitValue) {
                ""
            } else {
                entry
            }
        } else {
            element.entry.subElement = entry
        }

        onEntryUpdate(element)
    }

    override fun onUserEntryComplete(element: EnergySurveyElementModel) {
        if (element.type == EnergySurveyElementType.QUANTITY || element.type == EnergySurveyElementType.QUANTITY0) {
            val value = try {
                element.entry.subElement.toDouble()
            } catch (e: Exception) {
                null
            }

            val formatter = DecimalFormat("#.##")

            value?.let {
                if (value > element.warnValueHigh) {
                    excessiveValueDetected.value = formatter.format(value) to element.titleShort
                } else if (value < element.warnValueLow) {
                    excessivelyLowValueDetected.value =
                        formatter.format(value) to element.titleShort
                }
            }
        }
    }

    private fun onEntryUpdate(element: EnergySurveyElementModel) {
        _groups.notify()

        saveEntry(element)

        updateSurveyCompletionStatus(
            SurveyType.ENERGY,
            groups.getNonNullValue().filterNot { it.isSkipped }.all { it.isComplete }
        )
    }

    private fun saveEntry(element: EnergySurveyElementModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val entryRepository = appContainer.energySurveyElementEntryRepository

            if (!element.isSkipped && element.isComplete) {
                entryRepository.insertEntry(
                    element.entry.apply {
                        details = getSurveyDetails(element.entry.details?.entryInstant)
                    }
                )
            } else {
                entryRepository.clearEntry(element.id, property.UPRN)
            }
        }
    }
}
