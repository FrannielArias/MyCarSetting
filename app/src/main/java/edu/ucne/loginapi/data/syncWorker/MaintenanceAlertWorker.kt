package edu.ucne.loginapi.data.syncWorker

import android.content.Context
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

    override suspend fun doWork(): Result {
        val tasks = maintenanceTaskDao.getAllTasksOnce()
        val now = System.currentTimeMillis()
        val upcomingWindow = now + TimeUnit.DAYS.toMillis(3)

        val relevant = tasks.filter { task ->
            task.dueDateMillis != null &&
                    !task.isPendingDelete
        }

        val overdue = relevant.filter { it.dueDateMillis!! < now }
        val upcoming = relevant.filter { it.dueDateMillis!! in now..upcomingWindow }

        if (overdue.isEmpty() && upcoming.isEmpty()) {
            return Result.success()
        }

        val criticalOverdue = overdue.filter {
            it.severity == "CRITICAL"
        }
        val highOverdue = overdue.filter {
            it.severity == "HIGH"
        }

        val manager = NotificationManagerCompat.from(applicationContext)

        if (criticalOverdue.isNotEmpty()) {
            val first = criticalOverdue.first()
            val message =
                if (criticalOverdue.size == 1) {
                    "La tarea crítica \"${first.title}\" está vencida."
                } else {
                    "Tienes ${criticalOverdue.size} tareas críticas vencidas. Ej: \"${first.title}\"."
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

        if (highOverdue.isNotEmpty()) {
            val first = highOverdue.first()
            val message =
                if (highOverdue.size == 1) {
                    "La tarea importante \"${first.title}\" está vencida."
                } else {
                    "Tienes ${highOverdue.size} tareas importantes vencidas. Ej: \"${first.title}\"."
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

        val generalUpcoming = upcoming.filter {
            it.severity == "LOW" || it.severity == "MEDIUM"
        }

        if (generalUpcoming.isNotEmpty()) {
            val first = generalUpcoming.first()
            val message =
                if (generalUpcoming.size == 1) {
                    "La tarea \"${first.title}\" se aproxima. Revisa tu mantenimiento."
                } else {
                    "Tienes ${generalUpcoming.size} tareas próximas. Ej: \"${first.title}\"."
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

        return Result.success()
    }
}