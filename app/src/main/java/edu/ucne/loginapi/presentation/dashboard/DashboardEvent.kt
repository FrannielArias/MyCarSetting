package edu.ucne.loginapi.presentation.dashboard

sealed interface DashboardEvent {
    object LoadInitialData : DashboardEvent
    object Refresh : DashboardEvent
    object OnUserMessageShown : DashboardEvent
}