package edu.ucne.loginapi.presentation.dashboard

import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.model.VehicleAlert

data class DashboardUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val isRefreshing: Boolean = false,
    val currentCar: UserCar? = null,
    val upcomingTasks: List<MaintenanceTask> = emptyList(),
    val overdueTasks: List<MaintenanceTask> = emptyList(),
    val alerts: List<VehicleAlert> = emptyList(),
    val userMessage: String? = null
)
