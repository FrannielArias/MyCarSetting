package edu.ucne.loginapi.presentation.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.MaintenanceSeverity
import edu.ucne.loginapi.domain.model.MaintenanceStatus
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.MaintenanceType
import edu.ucne.loginapi.domain.useCase.maintenance.MarkTaskCompletedUseCase
import edu.ucne.loginapi.domain.useCase.maintenance.ObserveOverdueTasksForCarUseCase
import edu.ucne.loginapi.domain.useCase.maintenance.ObserveUpcomingTasksForCarUseCase
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
    private val observeUpcomingUseCase: ObserveUpcomingTasksForCarUseCase,
    private val observeOverdueUseCase: ObserveOverdueTasksForCarUseCase,
    private val createTaskUseCase: CreateMaintenanceTaskLocalUseCase,
    private val deleteTaskUseCase: DeleteMaintenanceTaskUseCase,
    private val markCompletedUseCase: MarkTaskCompletedUseCase,
    private val triggerSyncUseCase: TriggerMaintenanceSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MaintenanceUiState())
    val state = _state.asStateFlow()

    private var upcomingJob: Job? = null
    private var overdueJob: Job? = null

    init { loadInitial() }

    fun onEvent(event: MaintenanceEvent) {
        when (event) {

            is MaintenanceEvent.LoadInitialData -> loadInitial()
            is MaintenanceEvent.Refresh -> refresh()

            is MaintenanceEvent.ShowCreateSheet -> _state.update { it.copy(showCreateSheet = true) }
            is MaintenanceEvent.HideCreateSheet -> resetCreateSheet()

            is MaintenanceEvent.OnNewTitleChange -> validateTitle(event.value)
            is MaintenanceEvent.OnNewDescriptionChange -> _state.update { it.copy(newTaskDescription = event.value) }
            is MaintenanceEvent.OnNewDueMileageChange -> _state.update { it.copy(newTaskDueMileage = event.value) }

            is MaintenanceEvent.OnNewDueDateSelected -> _state.update {
                it.copy(newTaskDueDateMillis = event.millis, newTaskDueDateText = event.formatted)
            }

            is MaintenanceEvent.OnClearNewDueDate -> _state.update {
                it.copy(newTaskDueDateMillis = null, newTaskDueDateText = "")
            }

            is MaintenanceEvent.OnNewSeveritySelected -> _state.update { it.copy(newTaskSeverity = event.severity) }

            is MaintenanceEvent.ShowCompleteTaskDialog -> showCompleteDialog(event.taskId)
            is MaintenanceEvent.HideCompleteTaskDialog -> hideCompleteDialog()

            is MaintenanceEvent.OnCostAmountChange -> validateCost(event.value)
            is MaintenanceEvent.ConfirmCompleteTask -> completeTask()

            is MaintenanceEvent.OnDeleteTask -> deleteTask(event.taskId)

            is MaintenanceEvent.OnUserMessageShown -> _state.update { it.copy(userMessage = null) }

            is MaintenanceEvent.OnTaskClicked -> Unit
        }
    }
    private fun loadInitial() {
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
            observeTasks(car.id)

            triggerSyncUseCase()
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
                triggerSyncUseCase()
            } else {
                _state.update { it.copy(upcomingTasks = emptyList(), overdueTasks = emptyList()) }
            }

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun observeTasks(carId: Int) {
        upcomingJob?.cancel()
        overdueJob?.cancel()

        upcomingJob = viewModelScope.launch {
            observeUpcomingUseCase(carId).collectLatest { tasks ->
                _state.update { it.copy(upcomingTasks = tasks) }
            }
        }

        overdueJob = viewModelScope.launch {
            observeOverdueUseCase(carId).collectLatest { tasks ->
                _state.update { it.copy(overdueTasks = tasks) }
            }
        }
    }

    fun createTask() {
        val car = _state.value.currentCar ?: return
        val title = _state.value.newTaskTitle.trim()

        if (_state.value.newTitleError != null) {
            showMsg("Corrige el título antes de guardar")
            return
        }

        if (title.isBlank()) {
            showMsg("El título es requerido")
            return
        }

        val dueMillis = _state.value.newTaskDueDateMillis
            ?: return showMsg("Selecciona una fecha y hora objetivo")

        val mileage = _state.value.newTaskDueMileage.trim().toIntOrNull()

        val now = System.currentTimeMillis()

        val task = MaintenanceTask(
            carId = car.id,
            type = MaintenanceType.OIL_CHANGE,
            title = title,
            description = _state.value.newTaskDescription.ifBlank { null },
            dueDateMillis = dueMillis,
            dueMileageKm = mileage,
            severity = _state.value.newTaskSeverity,
            status = MaintenanceStatus.UPCOMING,
            createdAtMillis = now,
            updatedAtMillis = now
        )

        viewModelScope.launch {
            when (createTaskUseCase(task)) {
                is Resource.Success -> {
                    resetCreateSheet()
                    showMsg("Tarea creada localmente")
                    triggerSyncUseCase()
                }
                is Resource.Error -> showMsg("Error al crear tarea")
                else -> Unit
            }
        }
    }

    private fun completeTask() {
        val taskId = _state.value.taskToCompleteId ?: return
        val costText = _state.value.completeCostAmount.trim()

        val cost: Double? = when {
            costText.isBlank() -> null
            else -> {
                val parsedCost = costText.toDoubleOrNull()
                if (parsedCost == null) {
                    _state.update { it.copy(completeCostError = "Ingresa un número válido") }
                    return
                }
                if (parsedCost < 0) {
                    _state.update { it.copy(completeCostError = "El costo no puede ser negativo") }
                    return
                }
                parsedCost
            }
        }

        viewModelScope.launch {
            when (val result = markCompletedUseCase(taskId, System.currentTimeMillis(), cost)) {
                is Resource.Success -> {
                    hideCompleteDialog()
                    showMsg(
                        if (cost != null) "Tarea completada • Costo: $${String.format("%.2f", cost)}"
                        else "Tarea completada"
                    )
                    triggerSyncUseCase()
                }
                is Resource.Error -> {
                    showMsg("Error al completar tarea")
                }
                else -> Unit
            }
        }
    }

    private fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            when (deleteTaskUseCase(taskId)) {
                is Resource.Success -> showMsg("Tarea eliminada")
                is Resource.Error -> showMsg("Error al eliminar tarea")
                else -> Unit
            }
            triggerSyncUseCase()
        }
    }

    private fun showMsg(msg: String) {
        _state.update { it.copy(userMessage = msg) }
    }

    private fun resetCreateSheet() {
        _state.update {
            it.copy(
                showCreateSheet = false,
                newTaskTitle = "",
                newTaskDescription = "",
                newTaskDueMileage = "",
                newTaskDueDateMillis = null,
                newTaskDueDateText = "",
                newTaskSeverity = MaintenanceSeverity.MEDIUM,
                newTitleError = null
            )
        }
    }

    private fun showCompleteDialog(taskId: Int) {
        _state.update {
            it.copy(
                showCompleteTaskDialog = true,
                taskToCompleteId = taskId,
                completeCostAmount = "",
                completeCostError = null
            )
        }
    }

    private fun hideCompleteDialog() {
        _state.update {
            it.copy(
                showCompleteTaskDialog = false,
                taskToCompleteId = null,
                completeCostAmount = "",
                completeCostError = null
            )
        }
    }

    private fun validateTitle(value: String) {
        val isValid = value.length >= 5 &&
                value.matches(Regex("^[A-Za-z0-9ÁÉÍÓÚáéíóúñÑ ]+$"))

        _state.update {
            it.copy(
                newTaskTitle = value,
                newTitleError = if (!isValid && value.isNotEmpty())
                    "Debe tener mínimo 5 caracteres y solo letras/números"
                else null
            )
        }
    }

    private fun validateCost(value: String) {
        val cost = value.toDoubleOrNull()
        _state.update {
            it.copy(
                completeCostAmount = value,
                completeCostError = when {
                    value.isBlank() -> null
                    cost == null -> "Ingresa un número válido"
                    cost < 0 -> "El costo no puede ser negativo"
                    cost > 999999 -> "Costo demasiado alto"
                    else -> null
                }
            )
        }
    }
}
