package uk.co.savills.stonewood.repository

import android.annotation.SuppressLint
import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.model.survey.HHSRSSevereIssueModel
import uk.co.savills.stonewood.storage.db.dao.HHSRSSevereIssueDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

@SuppressLint("InvalidClassName")
class HHSRSSevereIssueRepository(
    private val dao: HHSRSSevereIssueDao
) {
    @WorkerThread
    fun insertIssue(entry: HHSRSSevereIssueModel) {
        dao.insertIssue(mapToEntity(entry))
    }

    @WorkerThread
    fun getAll(): List<HHSRSSevereIssueModel> {
        return dao.getAll().map(::mapToModel)
    }

    @WorkerThread
    fun getUnreportedIssues(): List<HHSRSSevereIssueModel> {
        return dao.getUnreportedIssues().map(::mapToModel)
    }

    @WorkerThread
    fun markAsReported(
        elementId: String,
        propertyId: Int,
        projectId: String
    ) {
        dao.getIssue(elementId, propertyId, projectId)?.apply {
            isReported = true
        }?.also(dao::insertIssue)
    }

    @WorkerThread
    fun getIssue(
        elementId: String,
        propertyId: Int,
        projectId: String
    ): HHSRSSevereIssueModel? {
        return dao.getIssue(elementId, propertyId, projectId)?.let { mapToModel(it) }
    }

    @WorkerThread
    fun clearIssue(
        elementId: String,
        propertyId: Int,
        projectId: String
    ) = dao.clearIssue(elementId, propertyId, projectId)

    @WorkerThread
    fun clearProjectEntries(ids: List<String>) = dao.clearProjectEntries(ids)

    @WorkerThread
    fun clearAll() = dao.clearAll()
}
