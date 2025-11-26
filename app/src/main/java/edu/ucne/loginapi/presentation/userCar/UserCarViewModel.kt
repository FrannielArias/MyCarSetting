package edu.ucne.loginapi.presentation.userCar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.useCase.user.AddUserCarUseCase
import edu.ucne.loginapi.domain.useCase.user.DeleteUserCarUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.GetCurrentCarUseCase
import edu.ucne.loginapi.domain.useCase.user.ObserveCarsUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.SetCurrentCarUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserCarViewModel @Inject constructor(
    private val observeCarsUseCase: ObserveCarsUseCase,
    private val addUserCarUseCase: AddUserCarUseCase,
    private val setCurrentCarUseCase: SetCurrentCarUseCase,
    private val deleteUserCarUseCase: DeleteUserCarUseCase,
    private val getCurrentCarUseCase: GetCurrentCarUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserCarUiState())
    val state: StateFlow<UserCarUiState> = _state.asStateFlow()

    init {
        onEvent(UserCarEvent.LoadInitialData)
    }

    fun onEvent(event: UserCarEvent) {
        when (event) {
            UserCarEvent.LoadInitialData -> loadInitial()
            UserCarEvent.ShowCreateSheet -> {
                _state.update { it.copy(showCreateSheet = true) }
            }
            UserCarEvent.HideCreateSheet -> {
                _state.update {
                    it.copy(
                        showCreateSheet = false,
                        brand = "",
                        model = "",
                        yearText = "",
                        plate = ""
                    )
                }
            }
            is UserCarEvent.OnBrandChange -> {
                _state.update { it.copy(brand = event.value) }
            }
            is UserCarEvent.OnModelChange -> {
                _state.update { it.copy(model = event.value) }
            }
            is UserCarEvent.OnYearChange -> {
                _state.update { it.copy(yearText = event.value) }
            }
            is UserCarEvent.OnPlateChange -> {
                _state.update { it.copy(plate = event.value) }
            }
            is UserCarEvent.OnFuelTypeChange -> {
                _state.update { it.copy(fuelType = event.value) }
            }
            is UserCarEvent.OnUsageTypeChange -> {
                _state.update { it.copy(usageType = event.value) }
            }
            is UserCarEvent.OnSetCurrentCar -> setCurrentCar(event.carId)
            is UserCarEvent.OnDeleteCar -> deleteCar(event.carId)
            UserCarEvent.OnSaveCar -> saveCar()
            UserCarEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
            is UserCarEvent.OnCarClicked -> {}
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val current = getCurrentCarUseCase()
            _state.update { it.copy(currentCarId = current?.id) }

            observeCarsUseCase().collectLatest { cars ->
                _state.update {
                    it.copy(
                        cars = cars,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun saveCar() {
        val brand = _state.value.brand.trim()
        val model = _state.value.model.trim()
        val yearText = _state.value.yearText.trim()

        if (brand.isBlank() || model.isBlank()) {
            _state.update { it.copy(userMessage = "Marca y modelo son requeridos") }
            return
        }

        val year = yearText.toIntOrNull()
        if (year == null || year < 1980 || year > 2100) {
            _state.update { it.copy(userMessage = "Año inválido") }
            return
        }

        val car = UserCar(
            id = UUID.randomUUID().toString(),
            brand = brand,
            model = model,
            year = year,
            plate = _state.value.plate.ifBlank { null },
            fuelType = _state.value.fuelType,
            usageType = _state.value.usageType,
            isCurrent = true
        )

        viewModelScope.launch {
            when (val result = addUserCarUseCase(car)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            showCreateSheet = false,
                            brand = "",
                            model = "",
                            yearText = "",
                            plate = "",
                            userMessage = "Vehículo guardado"
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            userMessage = result.message ?: "Error al guardar vehículo"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun setCurrentCar(carId: String) {
        viewModelScope.launch {
            when (val result = setCurrentCarUseCase(carId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            currentCarId = carId,
                            userMessage = "Vehículo seleccionado"
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            userMessage = result.message ?: "Error al seleccionar vehículo"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun deleteCar(carId: String) {
        viewModelScope.launch {
            when (val result = deleteUserCarUseCase(carId)) {
                is Resource.Success -> {
                    val newCurrent = if (_state.value.currentCarId == carId) null else _state.value.currentCarId
                    _state.update {
                        it.copy(
                            currentCarId = newCurrent,
                            userMessage = "Vehículo eliminado"
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            userMessage = result.message ?: "Error al eliminar vehículo"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
}