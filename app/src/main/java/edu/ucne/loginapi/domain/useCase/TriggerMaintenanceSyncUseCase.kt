package edu.ucne.loginapi.domain.useCase

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import edu.ucne.loginapi.data.MaintenanceSyncWorker

class TriggerMaintenanceSyncUseCase(
    private val context: Context
) {
    operator fun invoke() {
        val request = OneTimeWorkRequestBuilder<MaintenanceSyncWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
