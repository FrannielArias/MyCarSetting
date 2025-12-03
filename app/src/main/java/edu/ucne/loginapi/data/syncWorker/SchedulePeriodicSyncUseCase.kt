package edu.ucne.loginapi.domain.useCase

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import edu.ucne.loginapi.data.syncWorker.SyncWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SchedulePeriodicSyncUseCase @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        private const val UNIQUE_WORK_NAME = "periodic_full_sync"
    }

    operator fun invoke() {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(
            6, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}