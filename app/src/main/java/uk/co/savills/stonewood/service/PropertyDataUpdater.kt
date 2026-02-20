package uk.co.savills.stonewood.service

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import uk.co.savills.stonewood.AppContainer
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.MainApplication
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.repository.property.PropertyRepository
import uk.co.savills.stonewood.util.getWorkRequest
import uk.co.savills.stonewood.util.Result as ApiResult

class PropertyDataUpdater(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val appContainer: AppContainer
        get() = (applicationContext as MainApplication).appContainer

    private val propertyRepository: PropertyRepository
        get() = appContainer.propertyRepository

    private lateinit var projectId: String

    override suspend fun doWork(): Result {
        projectId = inputData.getString(PROJECT_ID_KEY) ?: return Result.success()

        return when (val result = appContainer.apiService.getUpdatedProperties(projectId)) {
            is ApiResult.Success -> {
                updateProperties(result.data)
                Result.success()
            }

            else -> Result.retry()
        }
    }

    private fun updateProperties(updatedProperties: List<PropertyModel>) {
        val properties = propertyRepository.getProperties(projectId)

        for (updatedProperty in updatedProperties) {
            val property = properties.find { it.id == updatedProperty.id } ?: continue

            if (updatedProperty.isDeleted) {
                propertyRepository.markPropertiesAsDeleted(projectId, updatedProperty.id)
            } else {
                property.contact = updatedProperty.contact
                propertyRepository.updateProperty(property)
            }
        }
    }

    companion object {
        private const val PROJECT_ID_KEY = "${BuildConfig.APPLICATION_ID}.id.project_id"

        fun update(
            context: Context,
            projectId: String
        ): LiveData<WorkInfo> {
            val request = getWorkRequest<PropertyDataUpdater>(
                workDataOf(PROJECT_ID_KEY to projectId)
            )

            val workManager = WorkManager.getInstance(context)

            workManager.beginUniqueWork(
                "PropertyDataUpdater",
                ExistingWorkPolicy.REPLACE,
                request
            ).enqueue()

            return workManager.getWorkInfoByIdLiveData(request.id)
        }
    }
}
