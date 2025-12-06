package edu.ucne.loginapi.presentation.Services

import ServiceCategory

sealed interface ServicesEvent {
    data object LoadInitialData : ServicesEvent
    data class LoadForLocation(val lat: Double, val lon: Double) : ServicesEvent
    data class OnCategorySelected(val category: ServiceCategory?) : ServicesEvent
    data class OnServiceClicked(val id: String) : ServicesEvent
    data object OnUserMessageShown : ServicesEvent
}
