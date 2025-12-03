// MaintenanceHistoryViewModel.kt
package edu.ucne.loginapi.presentation.maintenanceHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.syncWorker.TriggerFullSyncUseCase
import edu.ucne.loginapi.domain.useCase.MaintenanceHistory.GetMaintenanceHistoryForCarUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.GetCurrentCarUseCase
import edu.ucne.loginapi.domain.useCase.maintenance.DeleteMaintenanceRecordUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MaintenanceHistoryViewModel @Inject constructor(
    private val getCurrentCarUseCase: GetCurrentCarUseCase,
    private val getMaintenanceHistoryForCarUseCase: GetMaintenanceHistoryForCarUseCase,
    private val deleteMaintenanceRecordUseCase: DeleteMaintenanceRecordUseCase,
    private val triggerFullSyncUseCase: TriggerFullSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MaintenanceHistoryUiState())
    val state: StateFlow<MaintenanceHistoryUiState> = _state.asStateFlow()

    private var historyJob: Job? = null

    init {
        onEvent(MaintenanceHistoryEvent.LoadInitialData)
    }

    fun onEvent(event: MaintenanceHistoryEvent) {
        when (event) {
            MaintenanceHistoryEvent.LoadInitialData -> loadInitial()
            MaintenanceHistoryEvent.Refresh -> refresh()
            is MaintenanceHistoryEvent.OnDeleteRecord -> deleteRecord(event.id)
            is MaintenanceHistoryEvent.OnTypeFilterSelected -> {
                _state.update { it.copy(selectedType = event.type) }
            }
            MaintenanceHistoryEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            triggerFullSyncUseCase()

            val car = getCurrentCarUseCase()
            _state.update { it.copy(currentCar = car) }

            if (car != null) {
                observeHistory(car.id)
            } else {
                _state.update { it.copy(records = emptyList()) }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            triggerFullSyncUseCase()

            val car = getCurrentCarUseCase()
            _state.update { it.copy(currentCar = car) }

            if (car != null) {
                observeHistory(car.id)
            } else {
                _state.update { it.copy(records = emptyList()) }
            }

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun observeHistory(carId: Int) {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            getMaintenanceHistoryForCarUseCase(carId).collectLatest { list ->
                _state.update { it.copy(records = list) }
            }
        }
    }

    private fun deleteRecord(id: Int) {
        viewModelScope.launch {
            val result = deleteMaintenanceRecordUseCase(id)
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(userMessage = "Registro eliminado") }
                    triggerFullSyncUseCase()
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(userMessage = result.message ?: "Error al eliminar registro")
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
