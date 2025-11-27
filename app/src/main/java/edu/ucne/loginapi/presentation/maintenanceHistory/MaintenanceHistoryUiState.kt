package edu.ucne.loginapi.presentation.maintenanceHistory

import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.model.UserCar

data class MaintenanceHistoryUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val currentCar: UserCar? = null,
    val records: List<MaintenanceHistory> = emptyList(),
    val userMessage: String? = null
)
