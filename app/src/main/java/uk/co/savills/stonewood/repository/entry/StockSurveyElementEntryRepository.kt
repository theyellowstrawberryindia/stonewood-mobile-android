package uk.co.savills.stonewood.repository.entry

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.entry.StockSurveyElementEntryModel
import uk.co.savills.stonewood.storage.db.dao.entry.StockSurveyElementEntryDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

class StockSurveyElementEntryRepository(
    private val dao: StockSurveyElementEntryDao,
    private val appState: AppState
) {
    private val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun getEntries(projectId: String): List<StockSurveyElementEntryModel> {
        return dao.getEntries(projectId).map { mapToModel(it) }
    }

    @WorkerThread
    fun getEntries(propertyUPRN: String, projectId: String = this.projectId): List<StockSurveyElementEntryModel> {
        return dao.getEntries(projectId, propertyUPRN).map { mapToModel(it) }
    }

    @WorkerThread
    fun getEntries(elementId: Int, uprn: String): List<StockSurveyElementEntryModel> {
        val entries = dao.getEntries(elementId, projectId, uprn)
        return entries.map(::mapToModel)
    }

    @WorkerThread
    fun insertEntry(entry: StockSurveyElementEntryModel) {
        dao.insertEntry(mapToEntity(entry, projectId))
    }

    @WorkerThread
    fun clearEntry(elementId: Int, communalPartNumber: Int, uprn: String) =
        dao.clearEntry(elementId, communalPartNumber, projectId, uprn)

    @WorkerThread
    fun clearCommunalAreaEntries(communalPartNumbers: List<Int>) = dao.clearCommunalAreaEntries(communalPartNumbers, projectId)

    @WorkerThread
    fun clearEntries(propertyUPRNs: List<String>) {
        propertyUPRNs.chunked(100).forEach {
            dao.clearEntries(it, projectId)
        }
    }

    @WorkerThread
    fun clearProjectEntries(ids: List<String>) = dao.clearProjectEntries(ids)

    @WorkerThread
    fun clearAll() = dao.clearAll()
}
