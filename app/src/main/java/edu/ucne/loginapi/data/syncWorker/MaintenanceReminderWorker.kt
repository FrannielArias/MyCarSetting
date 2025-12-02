package edu.ucne.loginapi.data.syncWorker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import edu.ucne.loginapi.domain.useCase.ObserveOverdueTasksForCarUseCase
import edu.ucne.loginapi.domain.useCase.ObserveUpcomingTasksForCarUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.GetCurrentCarUseCase
import edu.ucne.loginapi.notifications.NotificationUtils
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class MaintenanceReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getCurrentCarUseCase: GetCurrentCarUseCase,
    private val observeUpcomingTasksForCarUseCase: ObserveUpcomingTasksForCarUseCase,
    private val observeOverdueTasksForCarUseCase: ObserveOverdueTasksForCarUseCase
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val car = getCurrentCarUseCase() ?: return Result.success()

        val overdueTasks = observeOverdueTasksForCarUseCase(car.id).first()
        val upcomingTasks = observeUpcomingTasksForCarUseCase(car.id).first()

        if (overdueTasks.isNotEmpty()) {
            val count = overdueTasks.size
            val title = "Tienes tareas de mantenimiento vencidas"
            val message = if (count == 1) {
                val task = overdueTasks.first()
                "La tarea \"${task.title}\" está vencida. Te recomendamos atenderla lo antes posible."
            } else {
                "Tienes $count tareas de mantenimiento vencidas en tu vehículo ${car.brand} ${car.model}."
            }

            NotificationUtils.showMaintenanceNotification(
                context = applicationContext,
                notificationId = 1001,
                title = title,
                message = message
            )

            return Result.success()
        }

        val now = System.currentTimeMillis()
        val soonWindowMillis = TimeUnit.DAYS.toMillis(3)
        val soonTasks = upcomingTasks.filter { task ->
            val dueDate = task.dueDateMillis
            dueDate != null && dueDate >= now && dueDate - now <= soonWindowMillis
        }

        if (soonTasks.isNotEmpty()) {
            val count = soonTasks.size
            val title = "Tienes mantenimientos próximos"
            val message = if (count == 1) {
                val task = soonTasks.first()
                "La tarea \"${task.title}\" está próxima. Considera agendarla en los próximos días."
            } else {
                "Tienes $count tareas de mantenimiento programadas en los próximos días para tu vehículo ${car.brand} ${car.model}."
            }

            NotificationUtils.showMaintenanceNotification(
                context = applicationContext,
                notificationId = 1002,
                title = title,
                message = message
            )
        }

        return Result.success()
    }
}
