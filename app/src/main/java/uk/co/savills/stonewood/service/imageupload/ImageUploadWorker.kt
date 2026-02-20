package uk.co.savills.stonewood.service.imageupload

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import uk.co.savills.stonewood.model.survey.project.ProjectModel
import uk.co.savills.stonewood.service.base.WorkerBase
import java.io.File
import java.util.concurrent.TimeUnit
import uk.co.savills.stonewood.util.Result as ApiResult

class ImageUploadWorker(
    context: Context,
    params: WorkerParameters
) : WorkerBase(context, params) {

    override suspend fun doWork(): Result {
        val projects = appContainer.projectRepository.getProjects().filterNot { it.isClosed }

        for (project in projects) {
            val history = appContainer.imageUploadHistoryRepository.get(project.id)
            val images = appContainer.getAllImages(project.id)

            if (uploadImages(images, project) !is ApiResult.Success) {
                return Result.retry()
            }

            val properties = appContainer.propertyRepository.getProperties(project.id)

            val externalImages = properties.flatMap { property ->
                property.extBlockPhotos.map { File(it) }
            }.filter {
                it.exists() && !history.contains(it.path)
            }

            if (uploadExtBlockPhotos(externalImages) !is ApiResult.Success) {
                return Result.retry()
            }
        }

        return Result.success()
    }

    private suspend fun uploadImages(
        images: MutableList<File>,
        project: ProjectModel
    ): ApiResult<Unit>? {
        if (images.isNotEmpty()) {
            images.chunked(IMAGE_UPLOAD_COUNT).forEach { chunk ->
                when (val result = appContainer.apiService.uploadImages(project.id, chunk)) {
                    is ApiResult.Success -> {
                        try {
                            appContainer.imageUploadHistoryRepository.insert(
                                chunk.map { it.path },
                                project.id
                            )
                        } catch (e: Exception) {
                            return ApiResult.Error(e)
                        }
                    }

                    else -> return result
                }
            }
        }
        return ApiResult.Success(Unit)
    }

    private suspend fun uploadExtBlockPhotos(images: List<File>): ApiResult<Unit> {
        if (images.any()) {
            images.chunked(IMAGE_UPLOAD_COUNT).forEach { chunk ->
                try {
                    val result = appContainer.apiService.uploadExtBlockImages(chunk)
                    if (result is ApiResult.Error) {
                        return result
                    }
                } catch (e: Exception) {
                    return ApiResult.Error(e)
                }
            }
        }

        return ApiResult.Success(Unit)
    }

    companion object {
        private val WORK_TAG = ImageUploadWorker::class.simpleName.orEmpty()

        private const val IMAGE_UPLOAD_COUNT = 10

        fun beginWork(context: Context) {
            val workConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val uploadWorkRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
                .setConstraints(workConstraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .addTag(WORK_TAG)
                .build()

            WorkManager.getInstance(context).beginUniqueWork(
                WORK_TAG,
                ExistingWorkPolicy.REPLACE,
                uploadWorkRequest
            ).enqueue()
        }

        fun cancel(context: Context) {
            try {
                WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG).result.get()
            } catch (e: Exception) {
                Unit
            }
        }
    }
}
