package edu.ucne.loginapi.presentation

import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.UserCar

data class DashboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val currentCar: UserCar? = null,
    val upcomingTasks: List<MaintenanceTask> = emptyList(),
    val overdueTasks: List<MaintenanceTask> = emptyList(),
    val userMessage: String? = null
)
