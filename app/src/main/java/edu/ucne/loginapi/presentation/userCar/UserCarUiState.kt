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
    val brand: String = "",
    val model: String = "",
    val yearText: String = "",
    val plate: String = "",
    val fuelType: FuelType = FuelType.GASOLINE,
    val usageType: UsageType = UsageType.PERSONAL,
    val userMessage: String? = null,
    val brands: List<VehicleBrand> = emptyList(),
    val models: List<VehicleModel> = emptyList(),
    val yearRanges: List<VehicleYearRange> = emptyList(),
    val selectedBrandId: String? = null,
    val selectedModelId: String? = null,
    val selectedYearRangeId: String? = null,
    val isLoadingCatalog: Boolean = false
)
