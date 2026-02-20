package uk.co.savills.stonewood.screen.survey.survey.stocksurvey

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.BandModel
import uk.co.savills.stonewood.model.survey.StockSurveyType
import uk.co.savills.stonewood.model.survey.element.StockSurveyElementGroupModel
import uk.co.savills.stonewood.model.survey.element.StockSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.StockSurveySubElementModel
import uk.co.savills.stonewood.model.survey.entry.CommunalDataModel
import uk.co.savills.stonewood.model.survey.entry.Date
import uk.co.savills.stonewood.model.survey.entry.StockSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.model.survey.property.PropertySurveyType
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyViewModelBase
import uk.co.savills.stonewood.service.imageupload.ImageUploadWorker
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.getNonNullValue
import uk.co.savills.stonewood.util.isNumeric
import uk.co.savills.stonewood.util.photo.getFileName
import uk.co.savills.stonewood.util.spitOrEmpty
import java.time.Instant
import java.time.LocalDate

class StockSurveyViewModel(
    application: Application,
    locationTracker: LocationTracker
) : SurveyViewModelBase(application, locationTracker), ElementUpdateListener {

    lateinit var ageBands: List<BandModel>
    lateinit var renewalBands: List<BandModel>
    lateinit var noAccessReasons: List<String>

    private val _groups = MutableLiveData<List<StockSurveyElementGroupModel>>()
    val groups: LiveData<List<StockSurveyElementGroupModel>>
        get() = _groups

    private val _selectedGroup = MutableLiveData<StockSurveyElementGroupModel>()
    val selectedGroup: LiveData<StockSurveyElementGroupModel>
        get() = _selectedGroup

    var ageOfProperty: Int = Int.MAX_VALUE

    val areRepairsAvailable = appState.currentProject.areRepairsAvailable

    val photoFolderName: String
        get() = "${appState.currentProject.id}_${appState.currentProperty.UPRN}"

    val addPhoto = SingleLiveEvent<Pair<String, (String) -> Unit>>()

    val elementUpdated = SingleLiveEvent<Int>()
    val groupUpdated = SingleLiveEvent<Nothing?>()

    private var blockCover = 0
    private var numberOfCommunalAreas = 1

    val excessiveValueDetected = SingleLiveEvent<Pair<String, String>>()
    val excessivelyLowValueDetected = SingleLiveEvent<Pair<String, String>>()

    val communalDataRequested = SingleLiveEvent<Pair<String, (CommunalDataModel) -> Unit>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAgeBands()
            getRenewalBands()
            getNoAccessReasons()
            getElementGroups()
        }
    }

    private fun getRenewalBands() {
        renewalBands =
            appContainer.renewalBandRepository.getBands().filterNot { it.upperBound == 0 }
    }

    private fun getAgeBands() {
        ageBands = appContainer.ageBandRepository.getBands().filterNot { it.upperBound == 0 }
    }

    private fun getNoAccessReasons() {
        noAccessReasons = appContainer.photoNoAccessReasonRepository.getReasons().map { it.reason }
    }

    private fun getElementGroups() {
        val propertySurveyType = appContainer.appState.currentProperty.surveyType

        val surveyTypes = when {
            listOf(
                PropertySurveyType.IE,
                PropertySurveyType.IESAP
            ).contains(propertySurveyType) -> listOf(
                StockSurveyType.INTERNAL,
                StockSurveyType.EXTERNAL,
                StockSurveyType.HOUSE_ONLY_EXTERNAL
            )

            listOf(
                PropertySurveyType.I,
                PropertySurveyType.ISAP
            ).contains(propertySurveyType) -> listOf(
                StockSurveyType.INTERNAL
            )

            propertySurveyType == PropertySurveyType.E -> listOf(
                StockSurveyType.EXTERNAL,
                StockSurveyType.HOUSE_ONLY_EXTERNAL
            )

            propertySurveyType == PropertySurveyType.EC -> listOf(
                StockSurveyType.BLOCK_ONLY_EXTERNAL,
                StockSurveyType.COMMUNAL,
                StockSurveyType.EXTERNAL
            )

            else -> listOf()
        }

        val elements = appContainer.stockSurveyElementRepository.getElements(surveyTypes)

        if (appContainer.appState.currentProperty.surveyType == PropertySurveyType.EC) {
            elements.forEach { it.unitTobeUsed = it.unitBlock }
        }

        setYearOfConstruction(elements)

        val groups =
            elements.filter { it.surveyType != StockSurveyType.COMMUNAL }.groupBy { element ->
                element.group
            }.map { map ->
                StockSurveyElementGroupModel(map.key, map.value.toMutableList())
            }.toMutableList()

        if (appContainer.appState.currentProperty.surveyType == PropertySurveyType.EC) {
            groups.addAll(getCommunalAreaGroups(elements))
        }

        val totalElements = mutableListOf<StockSurveyElementModel>()
        groups.forEach { totalElements.addAll(it.elements) }

        setupSkippedSubElements(totalElements)

        _groups.postValue(groups)

        if (groups.isNotEmpty()) {
            _selectedGroup.postValue(groups[0].apply { isSelected = true })
        }
    }

    private fun setupSkippedSubElements(elements: List<StockSurveyElementModel>) {
        val skipCodes = mutableListOf<Pair<Int, String>>()

        elements.forEach { element ->
            var subElement: StockSurveySubElementModel? = null

            element.subElements.forEach {
                it.isSkipped = false

                if (it.title == element.entry.subElement) {
                    subElement = it
                }
            }

            if (subElement != null) {
                val codes = requireNotNull(subElement).skippedElements.map {
                    element.entry.communalPartNumber to it
                }
                skipCodes.addAll(codes)
            }
        }

        for ((communalParNumber, skipCode) in skipCodes) {
            val isExcluding = skipCode.startsWith('!')

            var elementName = skipCode.substringBefore('(').trim()

            if (isExcluding) {
                elementName = elementName.removePrefix("!").trim()
            }

            val element = elements.find {
                it.title.equals(elementName, ignoreCase = true) &&
                    it.entry.communalPartNumber == communalParNumber
            } ?: continue

            val subElementNames = skipCode.substringAfter('(')
                .substringBefore(')', "")
                .spitOrEmpty(",")

            element.subElements.forEach { subElement ->
                val isInSkipCode =
                    subElementNames.any { it.equals(subElement.title, ignoreCase = true) }

                subElement.isSkipped =
                    subElementNames.isEmpty() || if (isExcluding) !isInSkipCode else isInSkipCode
            }
        }
    }

    private fun setYearOfConstruction(elements: List<StockSurveyElementModel>) {
        val iDocElement = elements.find { it.title == "iDoC" }
        if (iDocElement != null) {
            ageOfProperty = try {
                LocalDate.now().year - iDocElement.entry.subElement.toInt()
            } catch (e: NumberFormatException) {
                Int.MAX_VALUE
            }
        }
    }

    private fun getCommunalAreaGroups(elements: List<StockSurveyElementModel>): List<StockSurveyElementGroupModel> {
        val numberOfCommunalAreasElement =
            elements.find { it.title.equals("Number of communal areas", true) } ?: return listOf()

        val userEntry = numberOfCommunalAreasElement.entry.subElementUserEntry
        if (userEntry.isNotBlank()) numberOfCommunalAreas = userEntry.toInt()

        val defaultCommunalElements =
            elements.filter {
                it.surveyType == StockSurveyType.COMMUNAL && it.entry.communalPartNumber == 1
            }

        val groups = mutableListOf<StockSurveyElementGroupModel>()

        groups.add(
            StockSurveyElementGroupModel(
                "Communal area 1",
                defaultCommunalElements
            )
        )

        val elementsMap = mutableMapOf<Int, MutableList<StockSurveyElementModel>>()

        defaultCommunalElements.forEach { communalElement ->
            for (communalPartNumber in 2..numberOfCommunalAreas) {
                val savedCommunalElement =
                    elements.find { it.id == communalElement.id && it.entry.communalPartNumber == communalPartNumber }

                val element = savedCommunalElement ?: communalElement.copy(
                    elementEntry = StockSurveyElementEntryModel(
                        communalElement.id,
                        surveyType = communalElement.surveyType,
                        sequenceNumber = communalElement.sequenceNumber,
                        title = communalElement.title,
                        communalPartNumber = communalPartNumber,
                        isComplete = false
                    )
                ).apply {
                    unitTobeUsed = unitBlock
                    subElements = subElements.map { it.copy().apply { isSkipped = false } }
                }

                val communalElements = elementsMap.getOrElse(communalPartNumber, ::mutableListOf)
                communalElements.add(element)

                elementsMap[communalPartNumber] = communalElements
            }
        }

        elementsMap.map {
            groups.add(
                it.key - 1,
                StockSurveyElementGroupModel("Communal area ${it.key}", it.value)
            )
        }

        return groups
    }

    fun onGroupSelected(selectedGroup: StockSurveyElementGroupModel) {
        groups.getNonNullValue().forEach { group ->
            val isSelected = group.title == selectedGroup.title
            group.isSelected = isSelected

            groupUpdated.call()

            if (isSelected) setSelectedGroup(group)
        }
    }

    private fun setSelectedGroup(group: StockSurveyElementGroupModel) {
        group.isSelected = true
        _selectedGroup.postValue(group)
    }

    override fun onSubElementSelected() {
        val elements = mutableListOf<StockSurveyElementModel>()

        groups.getNonNullValue().forEach { group ->
            elements.addAll(group.elements)
        }

        setupSkippedSubElements(elements)

        for (element in elements) {
            if (element.isSkipped) {
                element.setAsSkipped()

                viewModelScope.launch(Dispatchers.IO) {
                    appContainer.stockStandardSurveyElementEntryRepository.insertEntry(
                        element.entry.apply {
                            details = getSurveyDetails()
                        }
                    )
                    if (element.entry.imagePaths.any()) ImageUploadWorker.beginWork(application)
                }
            }
        }

        groups.getNonNullValue().forEach { group ->
            if (group.isSelected) _selectedGroup.value = group
        }
    }

    override fun onEntryUpdate(_element: StockSurveyElementModel) {
        val element = getElement(_element)

        element.entry = _element.entry

        val entry = element.entry
        val subElement = entry.subElement

        if (element.title == "iDoC") {
            ageOfProperty = try {
                LocalDate.now().year - subElement.toInt()
            } catch (e: NumberFormatException) {
                Int.MAX_VALUE
            }
        }

        var isSubElementValid = subElement.isNotBlank()

        if (isSubElementValid && entry.isUserEntryRequired) {
            val userEntry = entry.subElementUserEntry
            isSubElementValid = when {
                subElement.contains("positiveNumeric", ignoreCase = true) -> {
                    userEntry.isNumeric() &&
                        !userEntry.contains('-')
                }

                subElement.contains("numeric", ignoreCase = true) -> userEntry.isNumeric()

                else -> userEntry.isNotBlank()
            }
        }

        if (isSubElementValid && element.surveyType == StockSurveyType.BLOCK_ONLY_EXTERNAL) {
            if (entry.title.equals("Block cover", true)) blockCover =
                entry.subElementUserEntry.toInt()

            if (entry.title.equals("Number of communal areas", true)) {
                val numberOfAreas = entry.subElementUserEntry.toInt()
                if (numberOfCommunalAreas != numberOfAreas && numberOfAreas != 0) {
                    setupCommunalAreaGroups(numberOfAreas)
                }
            }
        }

        var isDateValid = true
        if (element.isDateEntry) {
            isDateValid = entry.date?.month != null && entry.date?.year != null
        }

        var arePhotosValid = true
        if (element.isPhotoRequired && entry.noAccessReason.isEmpty()) {
            arePhotosValid = entry.imagePaths.size >= entry.minimumPhotosRequired
        }

        var isExtraInfoValid = true
        if (element.isExtraInfoRequired) {
            val isAsBuiltValid = !element.isAsBuiltRequired || entry.asBuilt != null

            var isRenewalInfoValid = entry.lifeRenewalBand != null

            when {
                element.isRenewalQuantityRequired -> {
                    isRenewalInfoValid =
                        entry.lifeRenewalBand != null && entry.lifeRenewalUnits != null
                }

                element.unitTobeUsed == StockSurveyElementModel.UnitType.PP -> {
                    entry.lifeRenewalUnits = 1
                }

                element.unitTobeUsed == StockSurveyElementModel.UnitType.CV -> {
                    entry.lifeRenewalUnits = blockCover
                }
            }

            var isRepairValid = !areRepairsAvailable || entry.repair != null
            if (isRepairValid && entry.repair == true) {
                isRepairValid =
                    entry.repairSpotPrice != null && entry.repairDescription.isNotBlank()
            }

            isExtraInfoValid =
                isAsBuiltValid && entry.existingAgeBand != null && isRenewalInfoValid && isRepairValid
        }

        val isRenewalBandExtreme = element.isRenewalBandExtreme(renewalBands)
        val isNotesValid = !isRenewalBandExtreme || entry.description.isNotEmpty()

        element.entry.isComplete =
            isSubElementValid && isDateValid && arePhotosValid && isExtraInfoValid && isNotesValid
        groupUpdated.call()

        viewModelScope.launch(Dispatchers.IO) {
            if (isSubElementValid) {
                if (element.entry.imagePaths.any()) ImageUploadWorker.beginWork(application)
                appContainer.stockStandardSurveyElementEntryRepository.insertEntry(
                    element.entry.apply {
                        details = getSurveyDetails(element.entry.details?.entryInstant)
                    }
                )
            } else {
                appContainer.stockStandardSurveyElementEntryRepository.clearEntry(
                    element.entry.elementId,
                    element.entry.communalPartNumber,
                    property.UPRN
                )
            }

            if (element.isCommunal && element.entry.isIndividual == false && entry.isComplete && entry.isCloned == false) {
                appContainer.communalDataRepository.insertEntry(
                    appState.currentProject.id,
                    CommunalDataModel(
                        null,
                        element.title,
                        property.UPRN,
                        property.address,
                        appState.profile?.fullName.orEmpty(),
                        entry.communalPartNumber,
                        requireNotNull(entry.subElementNumber),
                        entry.subElement,
                        entry.subElementUserEntry,
                        entry.description,
                        entry.repair,
                        entry.repairDescription,
                        entry.repairSpotPrice,
                        entry.lifeRenewalBand,
                        entry.lifeRenewalUnits,
                        entry.asBuilt,
                        entry.imagePaths,
                        entry.noAccessReason,
                        entry.existingAgeBand,
                        Instant.now(),
                        null
                    )
                )
            } else {
                appContainer.communalDataRepository.deleteEntry(
                    element.title,
                    element.entry.communalPartNumber,
                    property.UPRN,
                    appState.currentProject.id
                )
            }

            updateSurveyCompletionStatus(
                SurveyType.STOCK,
                _groups.getNonNullValue().all { group -> group.isComplete }
            )
        }
    }

    override fun onRenewalQuantityUpdate(element: StockSurveyElementModel) {
        val value = element.entry.lifeRenewalUnits ?: return

        if (value > element.warnValueHigh) {
            excessiveValueDetected.value = value.toString() to element.title
        } else if (value < element.warnValueLow) {
            excessivelyLowValueDetected.value = value.toString() to element.title
        }
    }

    private fun setupCommunalAreaGroups(numberOfAreas: Int) {
        when {
            numberOfCommunalAreas < numberOfAreas -> {
                val groups = mutableListOf<StockSurveyElementGroupModel>()
                for (communalPartNumber in (numberOfCommunalAreas + 1)..numberOfAreas) {
                    groups.add(getCommunalAreaGroup(communalPartNumber))
                }

                _groups.value = _groups.getNonNullValue().toMutableList().apply { addAll(groups) }
            }

            else -> {
                val communalAreasToBeRemoved =
                    (numberOfCommunalAreas downTo (numberOfAreas + 1)).toList()

                _groups.value = _groups.getNonNullValue().toMutableList().apply {
                    removeIf {
                        communalAreasToBeRemoved.any { communalPartNumber ->
                            it.title.contains("\\b(?:$communalPartNumber)\\b".toRegex())
                        }
                    }
                }

                viewModelScope.launch(Dispatchers.IO) {
                    appContainer.stockStandardSurveyElementEntryRepository.clearCommunalAreaEntries(
                        communalAreasToBeRemoved
                    )
                }
            }
        }

        numberOfCommunalAreas = numberOfAreas
    }

    private fun getCommunalAreaGroup(communalPartNumber: Int): StockSurveyElementGroupModel {
        val defaultCommunalElements =
            groups.getNonNullValue().find { it.title == "Communal area 1" }?.elements ?: listOf()

        return StockSurveyElementGroupModel(
            "Communal area $communalPartNumber",
            defaultCommunalElements.map { communalElement ->
                communalElement.copy(
                    elementEntry = StockSurveyElementEntryModel(
                        communalElement.id,
                        surveyType = communalElement.surveyType,
                        sequenceNumber = communalElement.sequenceNumber,
                        title = communalElement.title,
                        communalPartNumber = communalPartNumber,
                        isComplete = false
                    )
                ).apply {
                    unitTobeUsed = unitBlock
                    subElements = subElements.map { it.copy().apply { it.isSkipped = false } }
                }
            }
        )
    }

    override fun onAddPhoto(_element: StockSurveyElementModel, position: Int) {
        val element = getElement(_element)

        var elementNamePrefix = "Stock_"

        if (element.surveyType == StockSurveyType.COMMUNAL) {
            elementNamePrefix += "Com${element.entry.communalPartNumber}_"
        }

        val fileName = getFileName(
            requireNotNull(appState.profile).userName,
            property.UPRN,
            element.entry.imagePaths.size + 1,
            elementNamePrefix + element.title
        )

        addPhoto.value = fileName to { filePath: String ->
            element.entry.imagePaths.add(filePath)
            elementUpdated.value = position
            onEntryUpdate(element)
        }
    }

    override fun onPreviousCommunalDataRequest(_element: StockSurveyElementModel, position: Int) {
        val element = getElement(_element)

        communalDataRequested.value = element.title to { communalData ->
            val isSame = element.title == communalData.element &&
                element.entry.communalPartNumber == communalData.communalPartNumber &&
                property.UPRN == communalData.propertyUPRN

            element.entry.isCloned = !isSame
            with(element.entry) {
                subElement = communalData.subElement
                subElementNumber = communalData.subElementNumber
                subElementUserEntry = communalData.subElementUserEntry
                description = communalData.description
                repair = communalData.repair
                repairDescription = communalData.repairDescription
                repairSpotPrice = communalData.repairSpotPrice
                lifeRenewalBand = communalData.lifeRenewalBand
                lifeRenewalUnits = communalData.lifeRenewalUnits
                asBuilt = communalData.asBuilt
                imagePaths.clear()
                imagePaths.addAll(communalData.imagePaths)
                noAccessReason = communalData.noAccessReason
                existingAgeBand = communalData.existingAgeBand
            }

            if (element.isDateEntry) {
                element.entry.date = Date.fromString(element.entry.subElementUserEntry)
            }

            onEntryUpdate(element)
            elementUpdated.value = position
        }
    }

    private fun getElement(element: StockSurveyElementModel): StockSurveyElementModel {
        return groups.getNonNullValue().first { group ->
            group.elements.any {
                it.id == element.id &&
                    it.entry.communalPartNumber == element.entry.communalPartNumber
            }
        }.elements.first {
            it.id == element.id &&
                it.entry.communalPartNumber == element.entry.communalPartNumber
        }
    }
}
