package uk.co.savills.stonewood.repository.entry

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.entry.EnergySurveyElementEntryModel
import uk.co.savills.stonewood.storage.db.dao.entry.EnergySurveyElementEntryDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

class EnergySurveyElementEntryRepository(
    private val elementEntryDao: EnergySurveyElementEntryDao,
    private val appState: AppState,
) {
    val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun getProjectEntries(projectId: String): List<EnergySurveyElementEntryModel> {
        return elementEntryDao.getEntries(projectId).map(::mapToModel)
    }

    @WorkerThread
    fun getEntries(propertyUPRN: String): List<EnergySurveyElementEntryModel> {
        return elementEntryDao.getEntries(projectId, propertyUPRN).map(::mapToModel)
    }

    @WorkerThread
    fun getEntry(elementId: Int, propertyUPRN: String): EnergySurveyElementEntryModel? {
        val entry = elementEntryDao.getEntry(
            elementId,
            projectId,
            propertyUPRN
        )
        return if (entry != null) mapToModel(entry) else null
    }

    @WorkerThread
    fun insertEntry(entry: EnergySurveyElementEntryModel) {
        elementEntryDao.insertEntry(mapToEntity(entry, projectId))
    }

    @WorkerThread
    fun clearEntry(elementId: Int, propertyUPRN: String) {
        elementEntryDao.clearEntry(elementId, propertyUPRN, projectId)
    }

    @WorkerThread
    fun clearEntries(propertyUPRNs: List<String>) {
        propertyUPRNs.chunked(100).forEach {
            elementEntryDao.clearEntries(it, projectId)
        }
    }

    @WorkerThread
    fun clearProjectEntries(ids: List<String>) = elementEntryDao.clearProjectEntries(ids)

    @WorkerThread
    fun clearAll() = elementEntryDao.clearAll()
}
