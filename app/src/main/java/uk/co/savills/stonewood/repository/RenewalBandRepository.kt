package uk.co.savills.stonewood.repository

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.BandModel
import uk.co.savills.stonewood.storage.db.dao.RenewalBandDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

class RenewalBandRepository(
    private val dao: RenewalBandDao,
    private val appState: AppState,
) {
    private val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun insertBands(bands: List<BandModel>) {
        dao.insertBands(
            bands.map { mapToEntity(it, projectId) }
        )
    }

    @WorkerThread
    fun getBands(): List<BandModel> {
        return dao.getBands(projectId).map { mapToModel(it) }
    }

    @WorkerThread
    fun clearBands() {
        dao.clearBands(projectId)
    }

    @WorkerThread
    fun clearProjectBands(ids: List<String>) = dao.clearProjectBands(ids)

    @WorkerThread
    fun clearAll() = dao.clearAll()
}
