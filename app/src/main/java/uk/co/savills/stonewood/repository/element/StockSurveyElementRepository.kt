package uk.co.savills.stonewood.repository.element

import android.content.Context
import androidx.annotation.StringRes
import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.model.survey.StockSurveyType
import uk.co.savills.stonewood.model.survey.element.StockSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.StockSurveySubElementModel
import uk.co.savills.stonewood.model.survey.entry.StockSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.property.PropertySurveyType
import uk.co.savills.stonewood.repository.entry.StockSurveyElementEntryRepository
import uk.co.savills.stonewood.storage.db.dao.element.StockSurveyElementDao
import uk.co.savills.stonewood.storage.db.dao.element.StockSurveySubElementDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel
import java.time.LocalDate

class StockSurveyElementRepository(
    private val context: Context,
    private val elementDao: StockSurveyElementDao,
    private val subElementDao: StockSurveySubElementDao,
    private val elementEntryRepository: StockSurveyElementEntryRepository,
    private val appState: AppState
) {
    private val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun getElement(
        projectId: String,
        element: String,
        propertyUPRN: String? = null,
    ): StockSurveyElementModel? {
        val elementEntity = elementDao.getElement(projectId, element) ?: return null
        val subElements = subElementDao.getElements(projectId, elementEntity.id)
        val entries = propertyUPRN?.let { elementEntryRepository.getEntries(elementEntity.id, it) }

        val model = mapToModel(elementEntity, subElements)

        model.entry = if (entries.isNullOrEmpty()) {
            StockSurveyElementEntryModel(
                model.id,
                surveyType = model.surveyType,
                sequenceNumber = model.sequenceNumber,
                title = model.title,
                communalPartNumber = if (model.surveyType == StockSurveyType.COMMUNAL) 1 else 0,
                isComplete = false
            )
        } else {
            entries[0]
        }

        return model
    }

    @WorkerThread
    fun getElements(surveyTypes: List<StockSurveyType>): List<StockSurveyElementModel> {
        val elements = getDefaultElements()

        elementDao.getElements(projectId, surveyTypes.map { it.title })
            .forEach { entity ->
                val subElements = subElementDao.getElements(projectId, entity.id)
                val element = mapToModel(entity, subElements)

                val entries =
                    elementEntryRepository.getEntries(
                        element.id,
                        appState.currentProperty.UPRN
                    )

                elements.add(
                    element.apply {
                        this.entry = if (entries.isNotEmpty()) {
                            entries[0]
                        } else {
                            StockSurveyElementEntryModel(
                                id,
                                surveyType = surveyType,
                                sequenceNumber = sequenceNumber,
                                title = title,
                                communalPartNumber = if (element.surveyType == StockSurveyType.COMMUNAL) 1 else 0,
                                isComplete = false
                            )
                        }
                    }
                )

                if (element.surveyType == StockSurveyType.COMMUNAL) {
                    for (i in 1 until entries.size) {
                        elements.add(element.copy(elementEntry = entries[i]))
                    }
                }
            }

        return elements
    }

    private fun getDefaultElements(): MutableList<StockSurveyElementModel> {
        val defaultElements = mutableListOf<StockSurveyElementModel>()
        val propertySurveyType = appState.currentProperty.surveyType

        if (propertySurveyType != PropertySurveyType.EC) {
            val stockSurveyType = if (propertySurveyType == PropertySurveyType.E) {
                StockSurveyType.EXTERNAL
            } else {
                StockSurveyType.INTERNAL
            }

            defaultElements.addAll(
                listOf(
                    StockSurveyElementModel(
                        950,
                        1,
                        getString(R.string.property_address_element_title),
                        getString(R.string.general_group_title),
                        stockSurveyType,
                        StockSurveyElementModel.UnitType.IO,
                        StockSurveyElementModel.UnitType.IO,
                        subElements = listOf(
                            StockSurveySubElementModel(
                                0,
                                0,
                                getString(R.string.input_sub_element_postfix),
                                listOf(),
                                0,
                                0,
                                StockSurveySubElementModel.Cost(0.0, 0.0, 0.0, 0.0)
                            )
                        )
                    ),
                    StockSurveyElementModel(
                        951,
                        2,
                        getString(R.string.idoc_element_title),
                        getString(R.string.general_group_title),
                        stockSurveyType,
                        StockSurveyElementModel.UnitType.IO,
                        StockSurveyElementModel.UnitType.IO,
                        subElements = getiDocSubElements()
                    )
                )
            )
        }

        val defaultElementsWithEntry = mutableListOf<StockSurveyElementModel>()

        defaultElements.forEach { element ->
            val entries =
                elementEntryRepository.getEntries(
                    element.id,
                    appState.currentProperty.UPRN
                )

            defaultElementsWithEntry.add(
                element.apply {
                    this.entry = if (entries.isNotEmpty()) {
                        entries[0]
                    } else {
                        StockSurveyElementEntryModel(
                            id,
                            surveyType = surveyType,
                            sequenceNumber = sequenceNumber,
                            title = title,
                            isComplete = false
                        )
                    }
                }
            )
        }

        return defaultElementsWithEntry
    }

    private fun getiDocSubElements(): List<StockSurveySubElementModel> {
        val iDoCSubElements = mutableListOf<StockSurveySubElementModel>()

        val currentYear = LocalDate.now().year
        for (i in 0..50) {
            iDoCSubElements.add(
                StockSurveySubElementModel(
                    i,
                    i,
                    "${currentYear - i}",
                    listOf(),
                    0,
                    0,
                    StockSurveySubElementModel.Cost(0.0, 0.0, 0.0, 0.0)
                )
            )
        }

        iDoCSubElements.add(
            StockSurveySubElementModel(
                51,
                51,
                getString(R.string.idoc_entry_sub_element_title),
                listOf(),
                0,
                0,
                StockSurveySubElementModel.Cost(0.0, 0.0, 0.0, 0.0)
            )
        )

        return iDoCSubElements
    }

    @WorkerThread
    fun insertElements(elements: List<StockSurveyElementModel>) {
        elementDao.insertElements(
            elements.map { mapToEntity(it, projectId) }
        )

        elements.map { element ->
            subElementDao.insertElements(
                element.subElements.map { mapToEntity(it, element.id, projectId) }
            )
        }
    }

    @WorkerThread
    fun clearElements() {
        elementDao.clearElements(projectId)
        subElementDao.clearElements(projectId)
    }

    @WorkerThread
    fun clearProjectElements(ids: List<String>) {
        elementDao.clearProjectElements(ids)
        subElementDao.clearProjectElements(ids)
    }

    @WorkerThread
    fun clearAll() {
        elementDao.clearAll()
        subElementDao.clearAll()
    }

    private fun getString(@StringRes resId: Int) = context.getString(resId)
}
