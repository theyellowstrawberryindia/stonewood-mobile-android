package uk.co.savills.stonewood.repository.element

import android.annotation.SuppressLint
import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementModel
import uk.co.savills.stonewood.model.survey.entry.EnergySurveyElementEntryModel
import uk.co.savills.stonewood.repository.entry.EnergySurveyElementEntryRepository
import uk.co.savills.stonewood.storage.db.dao.element.EnergySurveyElementDao
import uk.co.savills.stonewood.storage.db.dao.element.EnergySurveySubElementDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

@SuppressLint("InvalidMethodName")
class EnergySurveyElementRepository(
    private val elementDao: EnergySurveyElementDao,
    private val subElementDao: EnergySurveySubElementDao,
    private val entryRepository: EnergySurveyElementEntryRepository,
    private val appState: AppState
) {
    private val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun getElement(
        projectId: String,
        element: String,
        propertyUPRN: String? = null
    ): EnergySurveyElementModel? {
        val elementEntity = elementDao.getElement(projectId, element) ?: return null
        val subElements = subElementDao.getElements(projectId, elementEntity.id).map(::mapToModel)

        val entry = propertyUPRN?.let {
            entryRepository.getEntry(elementEntity.id, it)
        }

        return mapToModel(elementEntity).apply {
            this.subElements.addAll(subElements)
            this.entry = entry ?: EnergySurveyElementEntryModel(id, titleShort)
        }
    }

    @WorkerThread
    fun getElements(): List<EnergySurveyElementModel> {
        return elementDao.getElements(projectId).map { element ->
            val subElements = subElementDao.getElements(projectId, element.id).map(::mapToModel)
            val entry = entryRepository.getEntry(element.id, appState.currentProperty.UPRN)

            mapToModel(element).apply {
                this.subElements.addAll(subElements)
                this.entry = entry ?: EnergySurveyElementEntryModel(id, titleShort)
            }
        }
    }

    @WorkerThread
    fun insertElements(elements: List<EnergySurveyElementModel>) {
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
}
