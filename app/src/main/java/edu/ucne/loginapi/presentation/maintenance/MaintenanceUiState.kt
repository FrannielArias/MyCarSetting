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

    // Create sheet
    val showCreateSheet: Boolean = false,
    val newTaskTitle: String = "",
    val newTaskDescription: String = "",
    val newTaskDueMileage: String = "",
    val newTaskDueDateMillis: Long? = null,
    val newTaskDueDateText: String = "",
    val newTaskSeverity: MaintenanceSeverity = MaintenanceSeverity.MEDIUM,
    val newTitleError: String? = null,

    // ✅ NUEVO: Complete task dialog
    val showCompleteTaskDialog: Boolean = false,
    val taskToCompleteId: Int? = null,
    val completeCostAmount: String = "",
    val completeCostError: String? = null,

    val userMessage: String? = null
)
