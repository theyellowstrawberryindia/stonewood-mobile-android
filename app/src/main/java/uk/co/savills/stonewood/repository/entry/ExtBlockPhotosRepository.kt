package uk.co.savills.stonewood.repository.entry

import androidx.annotation.WorkerThread
import okhttp3.ResponseBody
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.service.ApiService
import uk.co.savills.stonewood.storage.db.dao.entry.ExtBlockPhotosDao
import uk.co.savills.stonewood.storage.db.entity.entry.ExtBlockPhotoEntity
import uk.co.savills.stonewood.util.Result
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ExtBlockPhotosRepository(
    private val apiService: ApiService,
    private val dao: ExtBlockPhotosDao
) {
    @WorkerThread
    fun getEntries(
        projectId: String,
        searchText: String = ""
    ): List<ExtBlockPhotoModel> {
        return dao.getEntries(projectId, searchText).map(::mapToModel)
    }

    @WorkerThread
    fun getNewEntries(
        projectId: String,
        properties: List<String>
    ): List<ExtBlockPhotoModel> {
        return dao.getNewEntries(projectId, properties).map(::mapToModel)
    }

    @WorkerThread
    fun insertEntries(projectId: String, entries: List<ExtBlockPhotoModel>) {
        dao.insertEntries(entries.map { mapToEntity(it, projectId) })
    }

    @WorkerThread
    fun insertEntry(projectId: String, entry: ExtBlockPhotoModel) {
        dao.insertEntry(mapToEntity(entry, projectId))
    }

    @WorkerThread
    fun updateNewEntries(projectId: String, properties: List<String>) {
        val externalPhotos = getNewEntries(projectId, properties)

        for (data in externalPhotos) {
            data.id = Instant.now().hashCode()
        }

        dao.insertEntries(externalPhotos.map { mapToEntity(it, projectId) })
    }

    @WorkerThread
    suspend fun downloadExtBlockPhotos(
        request: List<String>,
        directory: File
    ): Result<List<String>>? {
        return when (val result = apiService.getExtBlockImages(request)) {
            is Result.Success -> {
                try {
                    if (!directory.exists()) directory.mkdirs()

                    val filePaths = saveFiles(result.data, directory)
                    Result.Success(filePaths)
                } catch (exception: Exception) {
                    Result.Error(exception)
                }
            }
            is Result.Error -> result
            null -> null
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun saveFiles(body: ResponseBody, directory: File): List<String> {
        val filePaths = mutableListOf<String>()
        body.byteStream().use { inputStream ->
            ZipInputStream(inputStream).use { zipInputStream ->
                var zipEntry: ZipEntry? = zipInputStream.nextEntry

                while (zipEntry != null) {
                    val entryFile = File(directory, zipEntry.name)

                    FileOutputStream(entryFile).use { fileOutputStream ->
                        BufferedOutputStream(fileOutputStream).use { bufferedOutputStream ->
                            val buffer = ByteArray(1024)
                            var read: Int
                            while (zipInputStream.read(buffer).also { read = it } != -1) {
                                bufferedOutputStream.write(buffer, 0, read)
                            }

                            zipInputStream.closeEntry()
                        }
                    }

                    filePaths.add(entryFile.path)

                    zipEntry = zipInputStream.nextEntry
                }
            }
        }

        return filePaths
    }

    @WorkerThread
    fun updateEntry(
        projectId: String,
        property: PropertyModel,
        surveyor: String,
        imagePaths: List<String>
    ) {
        val entry = dao.getEntry(projectId, property.UPRN) ?: ExtBlockPhotoEntity(
            null,
            property.UPRN,
            projectId,
            property.address.number,
            property.address.line1,
            property.address.line2,
            property.address.line3,
            property.address.line4,
            property.address.postalCode,
            surveyor,
            "",
            Instant.now().toString(),
            null
        )

        entry.imagePaths = imagePaths.joinToString(",")
        dao.insertEntry(entry)
    }

    @WorkerThread
    fun clearEntry(projectId: String, propertyUPRN: String) =
        dao.clearEntry(projectId, propertyUPRN)

    @WorkerThread
    fun clearOldEntries(projectId: String) = dao.clearOldEntries(projectId)

    @WorkerThread
    fun clearProjectEntries(ids: List<String>) = dao.clearProjectEntries(ids)

    @WorkerThread
    fun clearAll() = dao.clearAll()
}
