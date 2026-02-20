package uk.co.savills.stonewood.repository.entry

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.entry.SurveyElementEntryModel
import uk.co.savills.stonewood.storage.db.dao.entry.SurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.entity.entry.SurveyElementEntryEntity

class SurveyElementEntryRepository<Entity, Model, Dao>(
    private val dao: Dao,
    private val appState: AppState,
    private val mapToModel: (Entity) -> Model,
    private val mapToEntity: (Model, String) -> Entity,
) where Entity : SurveyElementEntryEntity, Model : SurveyElementEntryModel, Dao : SurveyElementEntryDao<Entity> {
    private val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun getEntries(projectId: String): List<Model> {
        return dao.getEntries(projectId).map { mapToModel(it) }
    }

    @WorkerThread
    fun getPropertyEntries(uprn: String): List<Model> {
        return dao.getEntries(projectId, uprn).map { mapToModel(it) }
    }

    @WorkerThread
    fun getEntry(projectId: String, uprn: String, id: String): Model? {
        val entry = dao.getEntry(id, projectId, uprn)

        return if (entry != null) mapToModel(entry) else null
    }

    @WorkerThread
    fun insertEntry(entry: Model) {
        dao.insertEntry(mapToEntity(entry, projectId))
    }

    @WorkerThread
    fun updateEntry(entry: Model) {
        dao.updateEntry(mapToEntity(entry, projectId))
    }

    @WorkerThread
    fun clearEntry(id: String, uprn: String) = dao.clearEntry(id, projectId, uprn)

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
