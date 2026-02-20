package uk.co.savills.stonewood.repository

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.PhotoNoAccessReasonModel
import uk.co.savills.stonewood.storage.db.dao.PhotoNoAccessReasonDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

class PhotoNoAccessReasonRepository(
    private val dao: PhotoNoAccessReasonDao,
    private val appState: AppState,
) {
    private val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun insertReasons(reasons: List<PhotoNoAccessReasonModel>) {
        dao.insertReasons(
            reasons.map { mapToEntity(projectId, it) }
        )
    }

    @WorkerThread
    fun getReasons(): List<PhotoNoAccessReasonModel> {
        return dao.getReasons(projectId).map(::mapToModel)
    }

    @WorkerThread
    fun clearReasons() {
        dao.clearReasons(projectId)
    }

    @WorkerThread
    fun clearProjectReasons(ids: List<String>) = dao.clearProjectReasons(ids)

    @WorkerThread
    fun clearAll() = dao.clearAll()
}
