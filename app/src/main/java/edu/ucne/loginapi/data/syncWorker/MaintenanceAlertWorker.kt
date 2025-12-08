package edu.ucne.loginapi.data.syncWorker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import edu.ucne.franniel_arias_ap2_p2.R
import edu.ucne.loginapi.data.dao.MaintenanceTaskDao
import edu.ucne.loginapi.notifications.MaintenanceNotificationChannels
import java.util.concurrent.TimeUnit

@HiltWorker
class MaintenanceAlertWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val maintenanceTaskDao: MaintenanceTaskDao
) : CoroutineWorker(appContext, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val tasks = maintenanceTaskDao.getAllTasksOnce()
        val now = System.currentTimeMillis()
        val upcomingWindow = now + TimeUnit.DAYS.toMillis(3)

        val relevant = tasks.filter { task ->
            task.dueDateMillis != null && !task.isPendingDelete
        }

        val overdue = relevant.filter { it.dueDateMillis!! < now }
        val upcoming = relevant.filter { it.dueDateMillis!! in now..upcomingWindow }

        val criticalOverdue = overdue.filter { it.severity == "CRITICAL" }
        val highOverdue = overdue.filter { it.severity == "HIGH" }
        val generalUpcoming = upcoming.filter {
            it.severity == "LOW" || it.severity == "MEDIUM"
        }

        val manager = NotificationManagerCompat.from(applicationContext)

        notifyCriticalOverdue(
            manager = manager,
            count = criticalOverdue.size,
            firstTitle = criticalOverdue.firstOrNull()?.title
        )

        notifyHighOverdue(
            manager = manager,
            count = highOverdue.size,
            firstTitle = highOverdue.firstOrNull()?.title
        )

        notifyGeneralUpcoming(
            manager = manager,
            count = generalUpcoming.size,
            firstTitle = generalUpcoming.firstOrNull()?.title
        )

        return Result.success()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notifyCriticalOverdue(
        manager: NotificationManagerCompat,
        count: Int,
        firstTitle: String?
    ) {
        if (count <= 0 || firstTitle == null) return

        val message = if (count == 1) {
            "La tarea crítica \"$firstTitle\" está vencida."
        } else {
            "Tienes $count tareas críticas vencidas. Ej: \"$firstTitle\"."
        }

        val notification = NotificationCompat.Builder(
            applicationContext,
            MaintenanceNotificationChannels.CHANNEL_CRITICAL
        )
            .setSmallIcon(R.drawable.ic_car_notification)
            .setContentTitle("Mantenimiento CRÍTICO vencido")
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(2001, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notifyHighOverdue(
        manager: NotificationManagerCompat,
        count: Int,
        firstTitle: String?
    ) {
        if (count <= 0 || firstTitle == null) return

        val message = if (count == 1) {
            "La tarea importante \"$firstTitle\" está vencida."
        } else {
            "Tienes $count tareas importantes vencidas. Ej: \"$firstTitle\"."
        }

        val notification = NotificationCompat.Builder(
            applicationContext,
            MaintenanceNotificationChannels.CHANNEL_HIGH
        )
            .setSmallIcon(R.drawable.ic_car_notification)
            .setContentTitle("Mantenimiento importante vencido")
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(2002, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notifyGeneralUpcoming(
        manager: NotificationManagerCompat,
        count: Int,
        firstTitle: String?
    ) {
        if (count <= 0 || firstTitle == null) return

        val message = if (count == 1) {
            "La tarea \"$firstTitle\" se aproxima. Revisa tu mantenimiento."
        } else {
            "Tienes $count tareas próximas. Ej: \"$firstTitle\"."
        }

        val notification = NotificationCompat.Builder(
            applicationContext,
            MaintenanceNotificationChannels.CHANNEL_GENERAL
        )
            .setSmallIcon(R.drawable.ic_car_notification)
            .setContentTitle("Mantenimientos próximos")
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(2003, notification)
    }
}