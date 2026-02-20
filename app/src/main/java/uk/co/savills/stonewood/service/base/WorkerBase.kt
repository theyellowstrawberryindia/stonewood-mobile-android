package uk.co.savills.stonewood.service.base

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import uk.co.savills.stonewood.MainApplication

abstract class WorkerBase(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    protected val appContainer
        get() = (applicationContext as MainApplication).appContainer
}
