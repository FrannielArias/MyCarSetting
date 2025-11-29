package edu.ucne.loginapi.presentation.Services

sealed interface ServicesEvent {
    object LoadInitialData : ServicesEvent
    data class OnCategorySelected(val category: ServiceCategory?) : ServicesEvent
    object OnUserMessageShown : ServicesEvent
}
