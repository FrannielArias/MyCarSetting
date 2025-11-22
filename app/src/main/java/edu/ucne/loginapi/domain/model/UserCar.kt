package edu.ucne.loginapi.domain.model

data class UserCar(
    val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String?,
    val fuelType: FuelType,
    val usageType: UsageType,
    val isCurrent: Boolean,
    val remoteId: Long? = null
)

enum class FuelType {
    GASOLINE,
    DIESEL,
    LPG,
    ELECTRIC,
    HYBRID,
    OTHER
}

enum class UsageType {
    PERSONAL,
    RIDE_SHARING,
    WORK,
    DELIVERY,
    OTHER
}
