package edu.ucne.loginapi.domain.model

data class VehicleBrand(
    val id: String,
    val name: String
)

data class VehicleModel(
    val id: String,
    val brandId: String,
    val name: String
)

data class VehicleYearRange(
    val id: String,
    val modelId: String,
    val fromYear: Int,
    val toYear: Int
) {
    val label: String
        get() = if (fromYear == toYear) fromYear.toString() else "$fromYear-$toYear"
}
