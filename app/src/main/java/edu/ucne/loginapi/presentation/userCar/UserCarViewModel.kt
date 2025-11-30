// ========================================
// VIEWMODEL - ACTUALIZADO
// ========================================
package edu.ucne.loginapi.presentation.userCar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.model.VehicleBrand
import edu.ucne.loginapi.domain.model.VehicleModel
import edu.ucne.loginapi.domain.model.VehicleYearRange
import edu.ucne.loginapi.domain.useCase.Vehiculo.GetVehicleBrandsUseCase
import edu.ucne.loginapi.domain.useCase.Vehiculo.GetVehicleModelsByBrandUseCase
import edu.ucne.loginapi.domain.useCase.Vehiculo.GetVehicleYearRangesByModelUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.GetCurrentCarUseCase
import edu.ucne.loginapi.domain.useCase.currentCar.SetCurrentCarUseCase
import edu.ucne.loginapi.domain.useCase.user.AddUserCarUseCase
import edu.ucne.loginapi.domain.useCase.user.DeleteUserCarUseCase
import edu.ucne.loginapi.domain.useCase.user.ObserveCarsUseCase
import edu.ucne.loginapi.domain.validation.ValidateCarDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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
    private val getCurrentCarUseCase: GetCurrentCarUseCase,
    private val validateCarDataUseCase: ValidateCarDataUseCase,
    // ✅ Nuevos Use Cases para el catálogo
    private val getVehicleBrandsUseCase: GetVehicleBrandsUseCase,
    private val getVehicleModelsByBrandUseCase: GetVehicleModelsByBrandUseCase,
    private val getVehicleYearRangesByModelUseCase: GetVehicleYearRangesByModelUseCase
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
                loadBrands()
                _state.update { it.copy(showCreateSheet = true) }
            }

            UserCarEvent.HideCreateSheet -> {
                _state.update {
                    it.copy(
                        showCreateSheet = false,
                        selectedBrandId = null,
                        selectedModelId = null,
                        selectedYearRangeId = null,
                        models = emptyList(),
                        yearRanges = emptyList(),
                        plate = ""
                    )
                }
            }

            is UserCarEvent.OnBrandSelected -> onBrandSelected(event.brand)
            is UserCarEvent.OnModelSelected -> onModelSelected(event.model)
            is UserCarEvent.OnYearRangeSelected -> onYearRangeSelected(event.yearRange)

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

    // ✅ Cargar marcas desde la API
    private fun loadBrands() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCatalog = true) }
            try {
                val brands = getVehicleBrandsUseCase().first()
                _state.update {
                    it.copy(
                        brands = brands,
                        isLoadingCatalog = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingCatalog = false,
                        userMessage = "Error al cargar marcas: ${e.message}"
                    )
                }
            }
        }
    }

    // ✅ Cuando selecciona una marca, cargar sus modelos
    private fun onBrandSelected(brand: VehicleBrand) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedBrandId = brand.id,
                    selectedModelId = null,
                    selectedYearRangeId = null,
                    models = emptyList(),
                    yearRanges = emptyList(),
                    isLoadingCatalog = true
                )
            }

            try {
                val models = getVehicleModelsByBrandUseCase(brand.id).first()
                _state.update {
                    it.copy(
                        models = models,
                        isLoadingCatalog = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingCatalog = false,
                        userMessage = "Error al cargar modelos: ${e.message}"
                    )
                }
            }
        }
    }

    // ✅ Cuando selecciona un modelo, cargar sus rangos de años
    private fun onModelSelected(model: VehicleModel) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedModelId = model.id,
                    selectedYearRangeId = null,
                    yearRanges = emptyList(),
                    isLoadingCatalog = true
                )
            }

            try {
                val yearRanges = getVehicleYearRangesByModelUseCase(model.id).first()
                _state.update {
                    it.copy(
                        yearRanges = yearRanges,
                        isLoadingCatalog = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingCatalog = false,
                        userMessage = "Error al cargar años: ${e.message}"
                    )
                }
            }
        }
    }

    // ✅ Guardar el rango de año seleccionado
    private fun onYearRangeSelected(yearRange: VehicleYearRange) {
        _state.update {
            it.copy(selectedYearRangeId = yearRange.id)
        }
    }

    private fun saveCar() {
        val selectedBrandId = _state.value.selectedBrandId
        val selectedModelId = _state.value.selectedModelId
        val selectedYearRangeId = _state.value.selectedYearRangeId

        if (selectedBrandId == null || selectedModelId == null || selectedYearRangeId == null) {
            _state.update {
                it.copy(userMessage = "Debes seleccionar marca, modelo y año")
            }
            return
        }

        // Obtener los datos seleccionados
        val selectedBrand = _state.value.brands.find { it.id == selectedBrandId }
        val selectedModel = _state.value.models.find { it.id == selectedModelId }
        val selectedYearRange = _state.value.yearRanges.find { it.id == selectedYearRangeId }

        if (selectedBrand == null || selectedModel == null || selectedYearRange == null) {
            _state.update {
                it.copy(userMessage = "Error: datos de vehículo inválidos")
            }
            return
        }

        // Usar el año final del rango (toYear)
        val car = UserCar(
            id = UUID.randomUUID().toString(),
            brand = selectedBrand.name,
            model = selectedModel.name,
            year = selectedYearRange.toYear, // ✅ Usar toYear como representativo
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
                            selectedBrandId = null,
                            selectedModelId = null,
                            selectedYearRangeId = null,
                            models = emptyList(),
                            yearRanges = emptyList(),
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
                    val newCurrent =
                        if (_state.value.currentCarId == carId) null else _state.value.currentCarId
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