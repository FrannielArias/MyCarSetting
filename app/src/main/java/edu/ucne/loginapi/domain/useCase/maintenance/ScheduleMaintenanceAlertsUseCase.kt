package edu.ucne.loginapi.domain.useCase.maintenance

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import edu.ucne.loginapi.data.syncWorker.MaintenanceAlertWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleMaintenanceAlertsUseCase @Inject constructor(
    private val workManager: WorkManager
) {
    operator fun invoke() {
        val request = PeriodicWorkRequestBuilder<MaintenanceAlertWorker>(
            12, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "maintenance_alerts",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
