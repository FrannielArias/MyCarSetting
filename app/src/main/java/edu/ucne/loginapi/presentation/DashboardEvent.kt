package edu.ucne.loginapi.presentation

sealed interface DashboardEvent {
    object LoadInitialData : DashboardEvent
    object Refresh : DashboardEvent
    object OnUserMessageShown : DashboardEvent
}
