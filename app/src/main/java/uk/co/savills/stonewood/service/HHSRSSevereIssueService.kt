package uk.co.savills.stonewood.service

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import uk.co.savills.stonewood.MainApplication
import uk.co.savills.stonewood.domain.hhsrs.SevereIssueService
import uk.co.savills.stonewood.model.survey.HHSRSSevereIssueModel
import uk.co.savills.stonewood.repository.HHSRSSevereIssueRepository
import uk.co.savills.stonewood.util.getWorkRequest
import uk.co.savills.stonewood.util.Result as ApiResult

@SuppressLint("InvalidClassName")
class HHSRSSevereIssueService(
    private val appContext: Context,
    private val repository: HHSRSSevereIssueRepository
) : SevereIssueService {

    @WorkerThread
    override fun getIssue(projectId: String, propertyId: Int, elementId: String): HHSRSSevereIssueModel? {
        return repository.getIssue(elementId, propertyId, projectId)
    }

    @WorkerThread
    override fun saveIssue(issue: HHSRSSevereIssueModel) = repository.insertIssue(issue)

    override fun reportIssues() {
        val request = getWorkRequest<Worker>()

        WorkManager.getInstance(appContext)
            .beginUniqueWork(
                HHSRSSevereIssueService::class.java.name,
                ExistingWorkPolicy.REPLACE,
                request
            )
            .enqueue()
    }

    class Worker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val appContainer = (applicationContext as MainApplication).appContainer
            val issues = appContainer.hhsrsSevereIssueRepository.getUnreportedIssues()

            for (issue in issues) {
                when (appContainer.apiService.reportHHSRSSevereIssue(issue.projectId, issue)) {
                    is ApiResult.Success -> {
                        appContainer.hhsrsSevereIssueRepository.markAsReported(
                            issue.elementId,
                            issue.propertyId,
                            issue.projectId
                        )
                    }
                    else -> return Result.retry()
                }
            }

            return Result.success()
        }
    }
}
