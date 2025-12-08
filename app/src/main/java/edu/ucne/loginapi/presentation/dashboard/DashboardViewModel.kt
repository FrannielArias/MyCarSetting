package edu.ucne.loginapi.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.syncWorker.TriggerFullSyncUseCase
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.VehicleAlert
import edu.ucne.loginapi.domain.model.VehicleAlertLevel
import edu.ucne.loginapi.domain.useCase.session.GetSessionUseCase
import edu.ucne.loginapi.domain.useCase.maintenance.ObserveOverdueTasksForCarUseCase
import edu.ucne.loginapi.domain.useCase.maintenance.ObserveUpcomingTasksForCarUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.GetCurrentCarUseCase
import edu.ucne.loginapi.domain.useCase.maintenance.ScheduleMaintenanceAlertsUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentCarUseCase: GetCurrentCarUseCase,
    private val observeUpcomingTasksForCarUseCase: ObserveUpcomingTasksForCarUseCase,
    private val observeOverdueTasksForCarUseCase: ObserveOverdueTasksForCarUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val scheduleMaintenanceAlertsUseCase: ScheduleMaintenanceAlertsUseCase,
    private val triggerFullSyncUseCase: TriggerFullSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private var upcomingJob: Job? = null
    private var overdueJob: Job? = null
    private var alertsScheduled: Boolean = false

    init {
        observeSession()
        onEvent(DashboardEvent.LoadInitialData)
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.LoadInitialData -> loadInitial()
            DashboardEvent.Refresh -> refresh()
            DashboardEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun observeSession() {
        viewModelScope.launch {
            getSessionUseCase().collectLatest { session ->
                _state.update { it.copy(userName = session?.userName.orEmpty()) }
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            triggerFullSyncUseCase()

            val car = getCurrentCarUseCase()
            _state.update { it.copy(currentCar = car) }

            if (car != null) {
                if (!alertsScheduled) {
                    scheduleMaintenanceAlertsUseCase()
                    alertsScheduled = true
                }
                observeTasks(car.id)
            } else {
                _state.update {
                    it.copy(
                        upcomingTasks = emptyList(),
                        overdueTasks = emptyList(),
                        alerts = emptyList(),
                        isLoading = false
                    )
                }
                return@launch
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            triggerFullSyncUseCase()

            val car = getCurrentCarUseCase()
            _state.update { it.copy(currentCar = car) }

            if (car != null) {
                if (!alertsScheduled) {
                    scheduleMaintenanceAlertsUseCase()
                    alertsScheduled = true
                }
                observeTasks(car.id)
            } else {
                _state.update {
                    it.copy(
                        upcomingTasks = emptyList(),
                        overdueTasks = emptyList(),
                        alerts = emptyList()
                    )
                }
            }

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun observeTasks(carId: Int) {
        upcomingJob?.cancel()
        overdueJob?.cancel()

        upcomingJob = viewModelScope.launch {
            observeUpcomingTasksForCarUseCase(carId).collectLatest { tasks ->
                val current = _state.value
                val alerts = generateAlerts(
                    upcoming = tasks,
                    overdue = current.overdueTasks
                )
                _state.update {
                    it.copy(
                        upcomingTasks = tasks,
                        alerts = alerts
                    )
                }
            }
        }

        overdueJob = viewModelScope.launch {
            observeOverdueTasksForCarUseCase(carId).collectLatest { tasks ->
                val current = _state.value
                val alerts = generateAlerts(
                    upcoming = current.upcomingTasks,
                    overdue = tasks
                )
                _state.update {
                    it.copy(
                        overdueTasks = tasks,
                        alerts = alerts
                    )
                }
            }
        }
    }

    private fun generateAlerts(
        upcoming: List<MaintenanceTask>,
        overdue: List<MaintenanceTask>
    ): List<VehicleAlert> {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24L * 60L * 60L * 1000L

        val overdueAlerts = overdue.map { task ->
            createOverdueAlert(task, now, oneDayMillis)
        }

        val upcomingAlerts = upcoming.mapNotNull { task ->
            createUpcomingAlert(task, now, oneDayMillis)
        }

        val allAlerts = (overdueAlerts + upcomingAlerts).ifEmpty {
            listOf(createAllGoodAlert())
        }

        return allAlerts.sortedWith(alertComparator())
    }

    private fun createOverdueAlert(
        task: MaintenanceTask,
        now: Long,
        oneDayMillis: Long
    ): VehicleAlert {
        val daysLate = task.dueDateMillis
            ?.let { ((now - it) / oneDayMillis).coerceAtLeast(0) }
            ?: 0L

        val level = overdueLevel(daysLate)
        val title = overdueTitle(level)
        val message = overdueMessage(task, daysLate)

        return VehicleAlert(
            id = "overdue_${task.id}",
            level = level,
            title = title,
            message = message,
            relatedTaskId = task.id
        )
    }

    private fun overdueLevel(daysLate: Long): VehicleAlertLevel {
        return if (daysLate >= 7L) {
            VehicleAlertLevel.CRITICAL
        } else {
            VehicleAlertLevel.IMPORTANT
        }
    }

    private fun overdueTitle(level: VehicleAlertLevel): String {
        return when (level) {
            VehicleAlertLevel.CRITICAL -> "Tarea vencida crítica"
            VehicleAlertLevel.IMPORTANT -> "Tarea vencida"
            else -> "Tarea vencida"
        }
    }

    private fun overdueMessage(
        task: MaintenanceTask,
        daysLate: Long
    ): String {
        return buildString {
            append("La tarea \"${task.title}\" está vencida")
            if (daysLate > 0) append(" hace $daysLate días")
            if (task.dueMileageKm != null) {
                append(" y estaba programada a los ${task.dueMileageKm} km")
            }
            append(". Atiéndela lo antes posible.")
        }
    }

    private fun createUpcomingAlert(
        task: MaintenanceTask,
        now: Long,
        oneDayMillis: Long
    ): VehicleAlert? {
        val due = task.dueDateMillis ?: return null
        val daysUntil = (due - now) / oneDayMillis
        val level = upcomingLevel(daysUntil) ?: return null
        val title = upcomingTitle(level)
        val message = upcomingMessage(task, daysUntil)

        return VehicleAlert(
            id = "upcoming_${task.id}",
            level = level,
            title = title,
            message = message,
            relatedTaskId = task.id
        )
    }

    private fun upcomingLevel(daysUntil: Long): VehicleAlertLevel? {
        return when {
            daysUntil in 0..7 -> VehicleAlertLevel.IMPORTANT
            daysUntil in 8..30 -> VehicleAlertLevel.RECOMMENDATION
            else -> null
        }
    }

    private fun upcomingTitle(level: VehicleAlertLevel): String {
        return when (level) {
            VehicleAlertLevel.IMPORTANT -> "Mantenimiento próximo"
            VehicleAlertLevel.RECOMMENDATION -> "Mantenimiento recomendado"
            else -> "Mantenimiento"
        }
    }

    private fun upcomingMessage(
        task: MaintenanceTask,
        daysUntil: Long
    ): String {
        return buildString {
            append("La tarea \"${task.title}\" está prevista")
            when {
                daysUntil < 0 -> append(" muy pronto.")
                daysUntil == 0L -> append(" para hoy.")
                else -> append(" en aproximadamente $daysUntil días.")
            }
            if (task.dueMileageKm != null) {
                append(" Objetivo: ${task.dueMileageKm} km.")
            }
        }
    }

    private fun createAllGoodAlert(): VehicleAlert {
        return VehicleAlert(
            id = "info_all_good",
            level = VehicleAlertLevel.INFO,
            title = "Todo en orden",
            message = "No tienes tareas vencidas y tus mantenimientos próximos están bajo control."
        )
    }

    private fun alertComparator(): Comparator<VehicleAlert> {
        return compareBy<VehicleAlert> {
            when (it.level) {
                VehicleAlertLevel.CRITICAL -> 0
                VehicleAlertLevel.IMPORTANT -> 1
                VehicleAlertLevel.RECOMMENDATION -> 2
                VehicleAlertLevel.INFO -> 3
            }
        }.thenByDescending { it.createdAtMillis }
    }
}