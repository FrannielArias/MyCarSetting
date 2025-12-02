package edu.ucne.loginapi.presentation.userCar

import edu.ucne.loginapi.domain.model.FuelType
import edu.ucne.loginapi.domain.model.UsageType
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.model.VehicleBrand
import edu.ucne.loginapi.domain.model.VehicleModel
import edu.ucne.loginapi.domain.model.VehicleYearRange

data class UserCarUiState(
    val isLoading: Boolean = false,
    val cars: List<UserCar> = emptyList(),
    val currentCarId: String? = null,
    val showCreateSheet: Boolean = false,

    // ✅ Catálogo de vehículos desde API
    val brands: List<VehicleBrand> = emptyList(),
    val models: List<VehicleModel> = emptyList(),
    val yearRanges: List<VehicleYearRange> = emptyList(),

    // ✅ Selección actual (IDs como Int)
    val selectedBrandId: Int? = null,
    val selectedModelId: Int? = null,
    val selectedYearRangeId: Int? = null,

    // Datos del formulario
    val plate: String = "",
    val fuelType: FuelType = FuelType.GASOLINE,
    val usageType: UsageType = UsageType.PERSONAL,

    val isLoadingCatalog: Boolean = false,
    val userMessage: String? = null
)