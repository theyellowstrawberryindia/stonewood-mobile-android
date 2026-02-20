package uk.co.savills.stonewood.util

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import java.util.concurrent.TimeUnit

inline fun <reified T : ListenableWorker> getWorkRequest(data: Data? = null): OneTimeWorkRequest {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val builder = OneTimeWorkRequestBuilder<T>().apply {
        data?.let(::setInputData)
    }

    return builder
        .setConstraints(constraints)
        .setBackoffCriteria(
            BackoffPolicy.LINEAR,
            10L,
            TimeUnit.MINUTES
        )
        .build()
}
