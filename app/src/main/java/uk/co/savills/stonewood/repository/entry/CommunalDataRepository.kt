package uk.co.savills.stonewood.repository.entry

import android.content.Context
import androidx.annotation.WorkerThread
import okhttp3.ResponseBody
import uk.co.savills.stonewood.PHOTO_DIRECTORY
import uk.co.savills.stonewood.model.survey.ImageRequestModel
import uk.co.savills.stonewood.model.survey.entry.CommunalDataModel
import uk.co.savills.stonewood.service.ApiService
import uk.co.savills.stonewood.storage.db.dao.entry.CommunalDataDao
import uk.co.savills.stonewood.util.Result
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.time.Instant
import java.util.zip.ZipInputStream

class CommunalDataRepository(
    private val appContext: Context,
    private val apiService: ApiService,
    private val dao: CommunalDataDao
) {
    @WorkerThread
    fun getEntries(
        element: String,
        projectId: String,
        searchText: String = ""
    ): List<CommunalDataModel> {
        return dao.getEntries(element, projectId, searchText).map(::mapToModel)
    }

    @WorkerThread
    fun getNewEntries(
        projectId: String,
        propertyUPRNs: List<String>
    ): List<CommunalDataModel> {
        return dao.getNewEntries(projectId, propertyUPRNs).map(::mapToModel)
    }

    @WorkerThread
    fun insertEntries(projectId: String, entries: List<CommunalDataModel>) {
        dao.insertEntries(entries.map { mapToEntity(it, projectId) })
    }

    @WorkerThread
    fun insertEntry(projectId: String, entry: CommunalDataModel) {
        dao.insertEntry(mapToEntity(entry, projectId))
    }

    @WorkerThread
    fun deleteEntry(
        element: String,
        communalPartNumber: Int,
        propertyUPRN: String,
        projectId: String,
    ) {
        dao.delete(element, communalPartNumber, projectId, propertyUPRN)
    }

    @WorkerThread
    fun updatePhotoStorage(projectId: String, properties: List<String>) {
        val rootDirectory = File(appContext.filesDir, PHOTO_DIRECTORY)
        val communalPhotoDirectory = File(rootDirectory, COMMUNAL_PHOTOS_DIRECTORY)
        val parentDirectory = File(communalPhotoDirectory, projectId)

        val communalData = getNewEntries(projectId, properties)

        for (data in communalData) {
            val toBeRemoved = mutableListOf<String>()
            val toBeAdded = mutableListOf<String>()

            for (imagePath in data.imagePaths) {
                val currentDirectory = File(rootDirectory, "${projectId}_${data.propertyUPRN}")

                val file = currentDirectory.listFiles()?.find { it.path == imagePath }

                if (file != null) {
                    val destinationFile = File(parentDirectory, file.name)
                    val x = file.copyTo(destinationFile, overwrite = true)

                    toBeRemoved.add(imagePath)
                    toBeAdded.add(x.path)
                }
            }

            data.imagePaths.removeAll(toBeRemoved)
            data.imagePaths.addAll(toBeAdded)
        }

        dao.insertEntries(communalData.map { mapToEntity(it, projectId) })
    }

    @WorkerThread
    fun updateNewEntries(projectId: String, completedProperties: List<String>) {
        val communalData = getNewEntries(projectId, completedProperties)

        for (data in communalData) {
            data.id = Instant.now().hashCode()
        }

        dao.insertEntries(communalData.map { mapToEntity(it, projectId) })
    }

    @WorkerThread
    suspend fun downloadCommunalImage(request: ImageRequestModel, directory: File): Result<String>? {
        return when (val result = apiService.getCommunalImages(listOf(request))) {
            is Result.Success -> {
                try {
                    if (!directory.exists()) directory.mkdirs()

                    val filePath = saveFile(result.data, directory)

                    if (filePath != null) Result.Success(filePath) else Result.Error(FileNotFoundException())
                } catch (exception: Exception) {
                    Result.Error(exception)
                }
            }
            is Result.Error -> result
            null -> null
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun saveFile(body: ResponseBody, directory: File): String? {
        val entryFile: File
        body.byteStream().use { inputStream ->
            ZipInputStream(inputStream).use { zipInputStream ->
                val zipEntry = zipInputStream.nextEntry ?: return null
                entryFile = File(directory, zipEntry.name)

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
            }
        }

        return entryFile.path
    }

    @WorkerThread
    fun clearOldEntries(projectId: String) = dao.clearOldEntries(projectId)

    @WorkerThread
    fun clearProjectEntries(ids: List<String>) = dao.clearProjectEntries(ids)

    @WorkerThread
    fun clearAll() = dao.clearAll()

    companion object {
        const val COMMUNAL_PHOTOS_DIRECTORY = "communal_photos"
    }
}
