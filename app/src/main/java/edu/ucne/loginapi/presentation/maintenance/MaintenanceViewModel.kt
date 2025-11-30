package edu.ucne.loginapi.presentation.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.MaintenanceStatus
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.MaintenanceType
import edu.ucne.loginapi.domain.useCase.MarkTaskCompletedUseCase
import edu.ucne.loginapi.domain.useCase.ObserveOverdueTasksForCarUseCase
import edu.ucne.loginapi.domain.useCase.ObserveUpcomingTasksForCarUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.GetCurrentCarUseCase
import edu.ucne.loginapi.domain.useCase.maintenance.CreateMaintenanceTaskLocalUseCase
import edu.ucne.loginapi.domain.useCase.maintenance.DeleteMaintenanceTaskUseCase
import edu.ucne.loginapi.domain.useCase.maintenance.TriggerMaintenanceSyncUseCase
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MaintenanceViewModel @Inject constructor(
    private val getCurrentCarUseCase: GetCurrentCarUseCase,
    private val observeUpcomingTasksForCarUseCase: ObserveUpcomingTasksForCarUseCase,
    private val observeOverdueTasksForCarUseCase: ObserveOverdueTasksForCarUseCase,
    private val createMaintenanceTaskLocalUseCase: CreateMaintenanceTaskLocalUseCase,
    private val deleteMaintenanceTaskUseCase: DeleteMaintenanceTaskUseCase,
    private val markTaskCompletedUseCase: MarkTaskCompletedUseCase,
    private val triggerMaintenanceSyncUseCase: TriggerMaintenanceSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MaintenanceUiState())
    val state: StateFlow<MaintenanceUiState> = _state.asStateFlow()

    private var tasksJob: Job? = null
    private var overdueJob: Job? = null

    init {
        onEvent(MaintenanceEvent.LoadInitialData)
    }

    fun onEvent(event: MaintenanceEvent) {
        when (event) {
            is MaintenanceEvent.LoadInitialData -> loadInitialData()
            is MaintenanceEvent.Refresh -> refresh()

            is MaintenanceEvent.ShowCreateSheet -> {
                _state.update { it.copy(showCreateSheet = true) }
            }

            is MaintenanceEvent.HideCreateSheet -> {
                _state.update {
                    it.copy(
                        showCreateSheet = false,
                        newTaskTitle = "",
                        newTaskDescription = "",
                        newTaskDueMileage = "",
                        newTaskDueDateMillis = null,
                        newTaskDueDateText = ""
                    )
                }
            }

            is MaintenanceEvent.OnNewTitleChange -> {
                _state.update { it.copy(newTaskTitle = event.value) }
            }

            is MaintenanceEvent.OnNewDescriptionChange -> {
                _state.update { it.copy(newTaskDescription = event.value) }
            }

            is MaintenanceEvent.OnNewDueMileageChange -> {
                _state.update { it.copy(newTaskDueMileage = event.value) }
            }

            is MaintenanceEvent.OnNewDueDateSelected -> {
                _state.update {
                    it.copy(
                        newTaskDueDateMillis = event.millis,
                        newTaskDueDateText = event.formatted
                    )
                }
            }

            is MaintenanceEvent.OnClearNewDueDate -> {
                _state.update {
                    it.copy(
                        newTaskDueDateMillis = null,
                        newTaskDueDateText = ""
                    )
                }
            }

            is MaintenanceEvent.OnCompleteTask -> completeTask(event.taskId)
            is MaintenanceEvent.OnDeleteTask -> deleteTask(event.taskId)
            is MaintenanceEvent.OnTaskClicked -> Unit

            is MaintenanceEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val car = getCurrentCarUseCase()
            if (car == null) {
                _state.update {
                    it.copy(
                        currentCar = null,
                        upcomingTasks = emptyList(),
                        overdueTasks = emptyList(),
                        isLoading = false,
                        userMessage = "No hay vehículo configurado"
                    )
                }
                return@launch
            }

            _state.update { it.copy(currentCar = car) }
            observeTasksForCar(car.id)
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            val car = getCurrentCarUseCase()
            _state.update { it.copy(currentCar = car) }

            if (car != null) {
                observeTasksForCar(car.id)
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

    private fun observeTasksForCar(carId: String) {
        tasksJob?.cancel()
        overdueJob?.cancel()

        tasksJob = viewModelScope.launch {
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

    fun createTask() {
        val current = _state.value.currentCar ?: return
        val title = _state.value.newTaskTitle.trim()

        if (title.isBlank()) {
            _state.update { it.copy(userMessage = "El título es requerido") }
            return
        }

        val mileageText = _state.value.newTaskDueMileage.trim()
        val mileage = mileageText.toIntOrNull()
        val now = System.currentTimeMillis()

        val task = MaintenanceTask(
            carId = current.id,
            type = edu.ucne.loginapi.domain.model.MaintenanceType.OIL_CHANGE,
            title = title,
            description = _state.value.newTaskDescription.ifBlank { null },
            dueDateMillis = _state.value.newTaskDueDateMillis,
            dueMileageKm = mileage,
            status = MaintenanceStatus.UPCOMING,
            createdAtMillis = now,
            updatedAtMillis = now
        )

        viewModelScope.launch {
            val result = createMaintenanceTaskLocalUseCase(task)
            when (result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            showCreateSheet = false,
                            newTaskTitle = "",
                            newTaskDescription = "",
                            newTaskDueMileage = "",
                            newTaskDueDateMillis = null,
                            newTaskDueDateText = "",
                            userMessage = "Tarea creada localmente"
                        )
                    }
                    triggerMaintenanceSyncUseCase()
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            userMessage = result.message ?: "Error al crear tarea"
                        )
                    }
                }

                is Resource.Loading -> Unit
            }
        }
    }

    private fun completeTask(taskId: String) {
        viewModelScope.launch {
            val result = markTaskCompletedUseCase(taskId, System.currentTimeMillis())
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(userMessage = "Tarea completada") }
                    triggerMaintenanceSyncUseCase()
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            userMessage = result.message ?: "Error al completar tarea"
                        )
                    }
                }

                is Resource.Loading -> Unit
            }
        }
    }

    private fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val result = deleteMaintenanceTaskUseCase(taskId)
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(userMessage = "Tarea eliminada") }
                    triggerMaintenanceSyncUseCase()
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            userMessage = result.message ?: "Error al eliminar tarea"
                        )
                    }
                }

                is Resource.Loading -> Unit
            }
        }
    }
}
