package uk.co.savills.stonewood.repository

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.storage.db.dao.NoAccessReasonDao
import uk.co.savills.stonewood.storage.db.entity.NoAccessReasonEntity

class NoAccessReasonRepository(
    private val dao: NoAccessReasonDao,
    private val appState: AppState,
) {
    private val projectId
        get() = appState.currentProject.id

    @WorkerThread
    fun insertReasons(reasons: List<String>) {
        dao.insertReasons(
            reasons.map { NoAccessReasonEntity(projectId, it) }
        )
    }

    @WorkerThread
    fun getReasons(): List<String> {
        return dao.getReasons(projectId).map { it.reason }
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
