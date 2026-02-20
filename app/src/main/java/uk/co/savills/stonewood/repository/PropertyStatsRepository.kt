package uk.co.savills.stonewood.repository

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.model.survey.property.PropertyStatsModel
import uk.co.savills.stonewood.storage.db.dao.PropertyStatsDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

class PropertyStatsRepository(
    private val dao: PropertyStatsDao
) {
    @WorkerThread
    fun getStats(projectId: String): List<PropertyStatsModel> {
        return dao.getStats(projectId).map(::mapToModel)
    }

    @WorkerThread
    fun insertStats(projectId: String, stats: List<PropertyStatsModel>) {
        return dao.insertStats(stats.map { mapToEntity(it, projectId) })
    }

    @WorkerThread
    fun clearProjectEntries(ids: List<String>) {
        dao.clearProjectEntries(ids)
    }

    @WorkerThread
    fun clear(projectId: String) {
        dao.clear(projectId)
    }

    @WorkerThread
    fun clearAll() = dao.clearAll()
}
