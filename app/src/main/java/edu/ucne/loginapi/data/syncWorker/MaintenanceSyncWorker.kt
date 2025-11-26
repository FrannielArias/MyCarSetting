package edu.ucne.loginapi.data.syncWorker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class MaintenanceSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return Result.success()
    }
}