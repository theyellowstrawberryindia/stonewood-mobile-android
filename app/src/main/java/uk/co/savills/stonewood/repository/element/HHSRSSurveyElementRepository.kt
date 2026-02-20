package uk.co.savills.stonewood.repository.element

import android.annotation.SuppressLint
import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.element.HHSRSSurveyElementModel
import uk.co.savills.stonewood.model.survey.entry.HHSRSSurveyElementEntryModel
import uk.co.savills.stonewood.repository.entry.SurveyElementEntryRepository
import uk.co.savills.stonewood.storage.db.dao.element.HHSRSSurveyElementDao
import uk.co.savills.stonewood.storage.db.dao.entry.HHSRSSurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.entity.entry.HHSRSSurveyElementEntryEntity
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

@SuppressLint("InvalidClassName, InvalidMethodName")
class HHSRSSurveyElementRepository(
    private val elementDao: HHSRSSurveyElementDao,
    private val elementEntryRepository: SurveyElementEntryRepository<HHSRSSurveyElementEntryEntity, HHSRSSurveyElementEntryModel, HHSRSSurveyElementEntryDao>,
    private val appState: AppState,
) {
    private val projectId
        get() = appState.currentProject.id

    private val propertyUPRN
        get() = appState.currentProperty.UPRN

    @WorkerThread
    fun getElements(): List<HHSRSSurveyElementModel> {
        return elementDao.getElements(projectId).map { elementEntity ->
            val entry = elementEntryRepository.getEntry(projectId, propertyUPRN, elementEntity.id)

            mapToModel(elementEntity).apply {
                if (entry != null) {
                    this.entry = entry
                } else {
                    this.entry = HHSRSSurveyElementEntryModel(
                        elementEntity.id,
                        elementEntity.title,
                        isComplete = false
                    )
                }
            }
        }
    }

    @WorkerThread
    fun insertElements(elements: List<HHSRSSurveyElementModel>) {
        elementDao.insertElements(
            elements.map { mapToEntity(it, projectId) }
        )
    }

    @WorkerThread
    fun clearElements() = elementDao.clearElements(projectId)

    @WorkerThread
    fun clearProjectElements(ids: List<String>) = elementDao.clearProjectElements(ids)

    @WorkerThread
    fun clearAll() = elementDao.clearAll()
}
