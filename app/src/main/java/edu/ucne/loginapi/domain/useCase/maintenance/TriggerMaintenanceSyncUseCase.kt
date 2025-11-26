package edu.ucne.loginapi.domain.useCase.maintenance

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.loginapi.data.syncWorker.MaintenanceSyncWorker
import javax.inject.Inject

class TriggerMaintenanceSyncUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke() {
        val request = OneTimeWorkRequestBuilder<MaintenanceSyncWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}