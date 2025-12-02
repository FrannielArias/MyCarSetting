package edu.ucne.loginapi.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import edu.ucne.loginapi.data.syncWorker.MaintenanceReminderWorker
import java.util.concurrent.TimeUnit

object MaintenanceReminderScheduler {

    private const val UNIQUE_WORK_NAME = "maintenance_reminder_work"

    fun schedulePeriodic(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val request = PeriodicWorkRequestBuilder<MaintenanceReminderWorker>(
            12, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
