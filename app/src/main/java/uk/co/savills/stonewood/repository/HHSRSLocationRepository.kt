package uk.co.savills.stonewood.repository

import android.annotation.SuppressLint
import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.HHSRSLocationModel
import uk.co.savills.stonewood.storage.db.dao.HHSRSLocationDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

@SuppressLint("InvalidClassName")
class HHSRSLocationRepository(
    private val dao: HHSRSLocationDao,
    private val appState: AppState,
) {
    private val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun insertLocations(locations: List<HHSRSLocationModel>) {
        dao.insertLocations(
            locations.map { mapToEntity(it, projectId) }
        )
    }

    @WorkerThread
    fun getLocations(): List<HHSRSLocationModel> {
        return dao.getLocations(projectId).map { mapToModel(it) }
    }

    @WorkerThread
    fun clearLocations() {
        dao.clearLocations(projectId)
    }

    @WorkerThread
    fun clearProjectLocations(ids: List<String>) = dao.clearProjectLocations(ids)

    @WorkerThread
    fun clearAll() = dao.clearAll()
}
