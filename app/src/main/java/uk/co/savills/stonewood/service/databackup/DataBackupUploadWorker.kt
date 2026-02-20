package uk.co.savills.stonewood.service.databackup

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.service.base.WorkerBase
import java.io.File
import java.io.IOException
import java.time.Duration
import java.util.concurrent.TimeUnit
import uk.co.savills.stonewood.util.Result as ApiResult

class DataBackupUploadWorker(
    context: Context,
    params: WorkerParameters
) : WorkerBase(context, params) {

    override suspend fun doWork(): Result {
        val projectId = inputData.getString(PROJECT_ID_KEY) ?: return Result.success()

        val files = mutableListOf<File>()

        inputData.getString(NO_ACCESS_KEY)?.also {
            val file = File(it)
            if (file.exists()) files.add(file)
        }
        inputData.getString(HHSRS_KEY)?.also {
            val file = File(it)
            if (file.exists()) files.add(file)
        }
        inputData.getString(QUALITY_STANDARD_KEY)?.also {
            val file = File(it)
            if (file.exists()) files.add(file)
        }
        inputData.getString(RISK_ASSESSMENT_KEY)?.also {
            val file = File(it)
            if (file.exists()) files.add(file)
        }
        inputData.getString(SAP_KEY)?.also {
            val file = File(it)
            if (file.exists()) files.add(file)
        }
        inputData.getString(STOCK_KEY)?.also {
            val file = File(it)
            if (file.exists()) files.add(file)
        }
        inputData.getString(SURVEY_HEADER_KEY)?.also {
            val file = File(it)
            if (file.exists()) files.add(file)
        }

        if (files.isEmpty()) return Result.success()

        return when (val result = appContainer.apiService.backupData(projectId, files)) {
            is ApiResult.Success -> Result.success()
            is ApiResult.Error -> {
                if (runAttemptCount > 10) {
                    appContainer.errorReportingService.reportError(IOException(result.exception))

                    Result.success()
                }
                Result.retry()
            }
            else -> {
                if (runAttemptCount > 10) {
                    appContainer.errorReportingService.reportError(
                        IOException("Data backup failed more than 10 times")
                    )

                    Result.success()
                }
                Result.retry()
            }
        }
    }

    companion object {
        const val PROJECT_ID_KEY = "${BuildConfig.APPLICATION_ID}.id.project_id"
        const val NO_ACCESS_KEY = "${BuildConfig.APPLICATION_ID}.id.no_access_file"
        const val HHSRS_KEY = "${BuildConfig.APPLICATION_ID}.id.hhsrs_file"
        const val QUALITY_STANDARD_KEY = "${BuildConfig.APPLICATION_ID}.id.quality_standard_file"
        const val RISK_ASSESSMENT_KEY = "${BuildConfig.APPLICATION_ID}.id.risk_assessment_file"
        const val SAP_KEY = "${BuildConfig.APPLICATION_ID}.id.sap_file"
        const val STOCK_KEY = "${BuildConfig.APPLICATION_ID}.id.stock_file"
        const val SURVEY_HEADER_KEY = "${BuildConfig.APPLICATION_ID}.id.survey_header"

        private const val WORK_DELAY_MINS = 10L

        fun begin(
            context: Context,
            projectId: String,
            pathData: List<Pair<String, String>>
        ) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val builder = OneTimeWorkRequestBuilder<DataBackupUploadWorker>().apply {
                setInputData(workDataOf(*pathData.toTypedArray()))
            }

            val request = builder
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10L,
                    TimeUnit.MINUTES
                )
                .setInitialDelay(Duration.ofMinutes(WORK_DELAY_MINS))
                .build()

            WorkManager.getInstance(context)
                .beginUniqueWork(
                    "${DataBackupUploadWorker::class.simpleName}_$projectId",
                    ExistingWorkPolicy.REPLACE,
                    request
                )
                .enqueue()
        }
    }
}
