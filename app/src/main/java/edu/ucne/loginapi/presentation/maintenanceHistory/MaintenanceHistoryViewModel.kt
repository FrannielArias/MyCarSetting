package edu.ucne.loginapi.presentation.maintenanceHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.useCase.maintenance.DeleteMaintenanceRecordUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.GetCurrentCarUseCase
import edu.ucne.loginapi.domain.useCase.MaintenanceHistory.GetMaintenanceHistoryForCarUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaintenanceHistoryViewModel @Inject constructor(
    private val getCurrentCarUseCase: GetCurrentCarUseCase,
    private val getMaintenanceHistoryForCarUseCase: GetMaintenanceHistoryForCarUseCase,
    private val deleteMaintenanceRecordUseCase: DeleteMaintenanceRecordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MaintenanceHistoryUiState())
    val state: StateFlow<MaintenanceHistoryUiState> = _state.asStateFlow()

    init {
        onEvent(MaintenanceHistoryEvent.LoadInitialData)
    }

    fun onEvent(event: MaintenanceHistoryEvent) {
        when (event) {
            MaintenanceHistoryEvent.LoadInitialData -> loadInitial()
            MaintenanceHistoryEvent.Refresh -> refresh()
            is MaintenanceHistoryEvent.OnDeleteRecord -> deleteRecord(event.id)
            MaintenanceHistoryEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val car = getCurrentCarUseCase()
            _state.update { it.copy(currentCar = car) }
            if (car != null) observeHistory(car.id)
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            val car = _state.value.currentCar ?: getCurrentCarUseCase()
            if (car != null) {
                _state.update { it.copy(currentCar = car) }
                observeHistory(car.id)
            }
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun observeHistory(carId: String) {
        viewModelScope.launch {
            getMaintenanceHistoryForCarUseCase(carId).collectLatest { list ->
                _state.update { it.copy(records = list) }
            }
        }
    }

    private fun deleteRecord(id: String) {
        viewModelScope.launch {
            when (val result = deleteMaintenanceRecordUseCase(id)) {
                is Resource.Success -> {
                    _state.update { it.copy(userMessage = "Registro eliminado") }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(userMessage = result.message ?: "Error al eliminar registro")
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
}