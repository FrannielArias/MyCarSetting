package edu.ucne.loginapi.presentation.maintenanceHistory

sealed interface MaintenanceHistoryEvent {
    object LoadInitialData : MaintenanceHistoryEvent
    object Refresh : MaintenanceHistoryEvent
    data class OnDeleteRecord(val id: String) : MaintenanceHistoryEvent
    object OnUserMessageShown : MaintenanceHistoryEvent
}
