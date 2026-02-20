package uk.co.savills.stonewood.repository.entry

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.noaccess.NoAccessEntryModel
import uk.co.savills.stonewood.storage.db.dao.entry.NoAccessEntryDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

class NoAccessEntryRepository(
    private val dao: NoAccessEntryDao,
    private val appState: AppState,
) {
    private val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun getEntries(projectId: String): List<NoAccessEntryModel> {
        return dao.getEntries(projectId).map { mapToModel(it) }
    }

    @WorkerThread
    fun getPropertyEntries(propertyUPRN: String): List<NoAccessEntryModel> {
        return dao.getEntries(projectId, propertyUPRN).map { mapToModel(it) }
    }

    @WorkerThread
    fun insertEntry(entry: NoAccessEntryModel, propertyId: Int) {
        dao.insertEntry(mapToEntity(entry, projectId, propertyId))
    }

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
