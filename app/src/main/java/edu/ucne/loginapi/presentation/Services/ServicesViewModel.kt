package edu.ucne.loginapi.presentation.Services

import ServiceItem
import ServicesUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ServicesUiState())
    val state: StateFlow<ServicesUiState> = _state.asStateFlow()

    init {
        onEvent(ServicesEvent.LoadInitialData)
    }

    fun onEvent(event: ServicesEvent) {
        when (event) {
            ServicesEvent.LoadInitialData -> loadInitial()

            is ServicesEvent.OnCategorySelected -> {
                _state.update { it.copy(selectedCategory = event.category) }
            }

            is ServicesEvent.OnServiceClicked -> {
                _state.update { it.copy(userMessage = "Seleccionaste ${event.id}") }
            }

            ServicesEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            delay(400)

            val items = listOf(
                ServiceItem(
                    id = "1",
                    name = "Taller Rodríguez",
                    category = ServiceCategory.TALLER,
                    description = "Mecánica general, frenos y suspensión",
                    distanceText = "A 1.2 km",
                    isOpen = true,
                    latitude = 19.3030,
                    longitude = -70.2520
                ),
                ServiceItem(
                    id = "2",
                    name = "Cambio de aceite Express",
                    category = ServiceCategory.MANTENIMIENTO,
                    description = "Cambio de aceite y filtros sin cita",
                    distanceText = "A 850 m",
                    isOpen = true,
                    latitude = 19.2990,
                    longitude = -70.2500
                ),
                ServiceItem(
                    id = "3",
                    name = "Lavado AutoClean",
                    category = ServiceCategory.LAVADO,
                    description = "Lavado completo y detallado interior",
                    distanceText = "A 2.3 km",
                    isOpen = false,
                    latitude = 19.2985,
                    longitude = -70.2535
                ),
                ServiceItem(
                    id = "4",
                    name = "Gomera La Rápida",
                    category = ServiceCategory.EMERGENCIA,
                    description = "Gomas, pinchazos y alineación básica",
                    distanceText = "A 2.1 km",
                    isOpen = true,
                    latitude = 19.3020,
                    longitude = -70.2550
                )
            )

            _state.update {
                it.copy(
                    services = items,
                    isLoading = false
                )
            }
        }
    }
}
