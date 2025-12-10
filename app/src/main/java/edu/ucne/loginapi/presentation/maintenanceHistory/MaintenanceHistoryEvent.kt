package edu.ucne.loginapi.presentation.maintenanceHistory

import edu.ucne.loginapi.domain.model.MaintenanceType

sealed interface MaintenanceHistoryEvent {
    data object LoadInitialData : MaintenanceHistoryEvent
    data object Refresh : MaintenanceHistoryEvent
    data class OnDeleteRecord(val id: Int) : MaintenanceHistoryEvent
    data class OnFilterTextSelected(val filterText: String?) : MaintenanceHistoryEvent
    data object OnUserMessageShown : MaintenanceHistoryEvent
}