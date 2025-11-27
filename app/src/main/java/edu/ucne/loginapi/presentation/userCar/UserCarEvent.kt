package edu.ucne.loginapi.presentation.userCar

import edu.ucne.loginapi.domain.model.FuelType
import edu.ucne.loginapi.domain.model.UsageType
import edu.ucne.loginapi.domain.model.UserCar

sealed interface UserCarEvent {
    object LoadInitialData : UserCarEvent
    object ShowCreateSheet : UserCarEvent
    object HideCreateSheet : UserCarEvent
    data class OnBrandChange(val value: String) : UserCarEvent
    data class OnModelChange(val value: String) : UserCarEvent
    data class OnYearChange(val value: String) : UserCarEvent
    data class OnPlateChange(val value: String) : UserCarEvent
    data class OnFuelTypeChange(val value: FuelType) : UserCarEvent
    data class OnUsageTypeChange(val value: UsageType) : UserCarEvent
    data class OnSetCurrentCar(val carId: String) : UserCarEvent
    data class OnDeleteCar(val carId: String) : UserCarEvent
    object OnSaveCar : UserCarEvent
    object OnUserMessageShown : UserCarEvent
    data class OnCarClicked(val car: UserCar) : UserCarEvent
}
