package edu.ucne.loginapi.presentation.userCar

import edu.ucne.loginapi.domain.model.FuelType
import edu.ucne.loginapi.domain.model.UsageType
import edu.ucne.loginapi.domain.model.UserCar

data class UserCarUiState(
    val isLoading: Boolean = true,
    val cars: List<UserCar> = emptyList(),
    val currentCarId: String? = null,
    val showCreateSheet: Boolean = false,
    val brand: String = "",
    val model: String = "",
    val yearText: String = "",
    val plate: String = "",
    val fuelType: FuelType = FuelType.GASOLINE,
    val usageType: UsageType = UsageType.PERSONAL,
    val userMessage: String? = null
)