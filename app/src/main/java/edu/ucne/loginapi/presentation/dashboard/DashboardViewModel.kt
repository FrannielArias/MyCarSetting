package edu.ucne.loginapi.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.domain.useCase.GetSessionUseCase
import edu.ucne.loginapi.domain.useCase.ObserveOverdueTasksForCarUseCase
import edu.ucne.loginapi.domain.useCase.ObserveUpcomingTasksForCarUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.GetCurrentCarUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentCarUseCase: GetCurrentCarUseCase,
    private val observeUpcomingTasksForCarUseCase: ObserveUpcomingTasksForCarUseCase,
    private val observeOverdueTasksForCarUseCase: ObserveOverdueTasksForCarUseCase,
    private val getSessionUseCase: GetSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private var upcomingJob: Job? = null
    private var overdueJob: Job? = null

    init {
        observeSession()                 // 👈 nuevo
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

    private fun observeSession() {
        viewModelScope.launch {
            getSessionUseCase().collectLatest { session ->
                _state.update { it.copy(userName = session?.userName.orEmpty()) }
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val car = getCurrentCarUseCase()
            _state.update { it.copy(currentCar = car) }

            if (car != null) {
                observeTasks(car.id)
            } else {
                _state.update {
                    it.copy(
                        upcomingTasks = emptyList(),
                        overdueTasks = emptyList()
                    )
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            val car = getCurrentCarUseCase()
            _state.update { it.copy(currentCar = car) }

            if (car != null) {
                observeTasks(car.id)
            } else {
                _state.update {
                    it.copy(
                        upcomingTasks = emptyList(),
                        overdueTasks = emptyList()
                    )
                }
            }

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
