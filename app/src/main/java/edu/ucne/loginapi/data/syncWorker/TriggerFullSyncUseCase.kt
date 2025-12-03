package edu.ucne.loginapi.data.syncWorker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import javax.inject.Inject

class TriggerFullSyncUseCase @Inject constructor(
    private val workManager: WorkManager
) {

    operator fun invoke() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .build()

        workManager.enqueue(request)
    }
}
