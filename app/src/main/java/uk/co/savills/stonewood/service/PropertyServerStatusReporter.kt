package uk.co.savills.stonewood.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.MainApplication
import uk.co.savills.stonewood.model.survey.property.PropertySurveyStatus
import uk.co.savills.stonewood.util.getWorkRequest
import uk.co.savills.stonewood.util.Result as ApiResult

class PropertyServerStatusReporter(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val appContainer = (applicationContext as MainApplication).appContainer

        val projectId = inputData.getString(PROJECT_ID_KEY)
        val propertyId = inputData.getInt(PROPERTY_ID_KEY, -1)
        val status = inputData.getInt(PROPERTY_STATUS_KEY, -1)

        if (projectId == null || propertyId == -1 || status == -1) return Result.failure()

        val result = appContainer.apiService.updatePropertyStatus(
            projectId,
            propertyId,
            PropertySurveyStatus.values()[status]
        )

        return when (result) {
            is ApiResult.Success -> Result.success()
            else -> if (this.runAttemptCount >= 3) Result.failure() else Result.retry()
        }
    }

    companion object {
        private const val PROJECT_ID_KEY = "${BuildConfig.APPLICATION_ID}.id.project_id"
        private const val PROPERTY_ID_KEY = "${BuildConfig.APPLICATION_ID}.id.property_id"
        private const val PROPERTY_STATUS_KEY = "${BuildConfig.APPLICATION_ID}.id.property_status"

        fun report(
            context: Context,
            projectId: String,
            propertyId: Int,
            status: PropertySurveyStatus
        ) {
            val request = getWorkRequest<PropertyServerStatusReporter>(
                workDataOf(
                    PROJECT_ID_KEY to projectId,
                    PROPERTY_ID_KEY to propertyId,
                    PROPERTY_STATUS_KEY to status.ordinal
                )
            )

            WorkManager.getInstance(context)
                .beginUniqueWork(
                    "PropertyServerStatusReporter_${projectId}_$propertyId",
                    ExistingWorkPolicy.REPLACE,
                    request
                )
                .enqueue()
        }
    }
}
