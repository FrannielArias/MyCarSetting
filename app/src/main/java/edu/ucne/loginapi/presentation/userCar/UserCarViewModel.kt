package edu.ucne.loginapi.presentation.userCar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.useCase.currentCar.GetCurrentCarUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.SetCurrentCarUseCase
import edu.ucne.loginapi.domain.useCase.user.AddUserCarUseCase
import edu.ucne.loginapi.domain.useCase.user.DeleteUserCarUseCase
import edu.ucne.loginapi.domain.useCase.user.ObserveCarsUseCase
import edu.ucne.loginapi.domain.validation.ValidateCarDataUseCase
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class UserCarViewModel @Inject constructor(
    private val observeCarsUseCase: ObserveCarsUseCase,
    private val addUserCarUseCase: AddUserCarUseCase,
    private val setCurrentCarUseCase: SetCurrentCarUseCase,
    private val deleteUserCarUseCase: DeleteUserCarUseCase,
    private val getCurrentCarUseCase: GetCurrentCarUseCase,
    private val validateCarDataUseCase: ValidateCarDataUseCase
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
            is UserCarEvent.OnCarClicked -> Unit
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

        val validationResult = validateCarDataUseCase(brand, model, yearText)
        if (!validationResult.successful) {
            _state.update { it.copy(userMessage = validationResult.errorMessage) }
            return
        }

        val year = yearText.toInt()
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
            val result = addUserCarUseCase(car)
            when (result) {
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
                is Resource.Loading -> Unit
            }
        }
    }

    private fun setCurrentCar(carId: String) {
        viewModelScope.launch {
            val result = setCurrentCarUseCase(carId)
            when (result) {
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
                is Resource.Loading -> Unit
            }
        }
    }

    private fun deleteCar(carId: String) {
        viewModelScope.launch {
            val result = deleteUserCarUseCase(carId)
            when (result) {
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
                is Resource.Loading -> Unit
            }
        }
    }
}
