package edu.ucne.loginapi.presentation.maintenanceHistory

import edu.ucne.loginapi.domain.model.MaintenanceType

sealed interface MaintenanceHistoryEvent {
    object LoadInitialData : MaintenanceHistoryEvent
    object Refresh : MaintenanceHistoryEvent
    data class OnDeleteRecord(val id: String) : MaintenanceHistoryEvent
    data class OnTypeFilterSelected(val type: MaintenanceType?) : MaintenanceHistoryEvent
    object OnUserMessageShown : MaintenanceHistoryEvent
}