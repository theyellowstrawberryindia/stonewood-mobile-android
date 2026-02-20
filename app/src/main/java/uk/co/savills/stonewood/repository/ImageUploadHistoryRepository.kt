package uk.co.savills.stonewood.repository

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.storage.db.dao.ImageUploadDao
import uk.co.savills.stonewood.storage.db.entity.ImageUploadEntity

class ImageUploadHistoryRepository(
    private val imageUploadDao: ImageUploadDao
) {

    @WorkerThread
    fun insert(filePath: String, projectId: String) {
        imageUploadDao.insert(ImageUploadEntity(filePath, projectId))
    }

    @WorkerThread
    fun insert(filePaths: List<String>, projectId: String) {
        imageUploadDao.insert(filePaths.map { ImageUploadEntity(it, projectId) })
    }

    @WorkerThread
    fun get(projectId: String) = imageUploadDao.getHistory(projectId).map { it.filePath }

    @WorkerThread
    fun clear(projectId: String) = imageUploadDao.deleteHistory(projectId)

    @WorkerThread
    fun clearAll() = imageUploadDao.deleteAll()
}
