package edu.ucne.loginapi.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.domain.useCase.currentCar.GetCurrentCarUseCase
import edu.ucne.loginapi.domain.useCase.ObserveOverdueTasksForCarUseCase
import edu.ucne.loginapi.domain.useCase.ObserveUpcomingTasksForCarUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentCarUseCase: GetCurrentCarUseCase,
    private val observeUpcomingTasksForCarUseCase: ObserveUpcomingTasksForCarUseCase,
    private val observeOverdueTasksForCarUseCase: ObserveOverdueTasksForCarUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private var upcomingJob: Job? = null
    private var overdueJob: Job? = null

    init {
        onEvent(DashboardEvent.LoadInitialData)
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.LoadInitialData -> loadInitial()
            DashboardEvent.Refresh -> refresh()
            DashboardEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val car = getCurrentCarUseCase()
            _state.update { it.copy(currentCar = car) }

            if (car != null) observeTasks(car.id)

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            val car = getCurrentCarUseCase()
            if (car != null) observeTasks(car.id)

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun observeTasks(carId: String) {
        upcomingJob?.cancel()
        overdueJob?.cancel()

        upcomingJob = viewModelScope.launch {
            observeUpcomingTasksForCarUseCase(carId).collectLatest { tasks ->
                _state.update { it.copy(upcomingTasks = tasks) }
            }
        }

        overdueJob = viewModelScope.launch {
            observeOverdueTasksForCarUseCase(carId).collectLatest { tasks ->
                _state.update { it.copy(overdueTasks = tasks) }
            }
        }
    }
}