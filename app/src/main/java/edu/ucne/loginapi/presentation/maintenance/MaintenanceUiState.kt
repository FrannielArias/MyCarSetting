package edu.ucne.loginapi.presentation.maintenance

import edu.ucne.loginapi.domain.model.MaintenanceSeverity
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.UserCar

data class MaintenanceUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val currentCar: UserCar? = null,
    val upcomingTasks: List<MaintenanceTask> = emptyList(),
    val overdueTasks: List<MaintenanceTask> = emptyList(),
    val showCreateSheet: Boolean = false,
    val newTaskTitle: String = "",
    val newTaskDescription: String = "",
    val newTaskDueMileage: String = "",
    val newTaskDueDateMillis: Long? = null,
    val newTaskDueDateText: String = "",
    val newTaskSeverity: MaintenanceSeverity = MaintenanceSeverity.MEDIUM,
    val userMessage: String? = null,
    val newTitleError: String? = null

    )