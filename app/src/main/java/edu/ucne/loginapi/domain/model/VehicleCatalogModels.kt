package edu.ucne.loginapi.domain.model

data class VehicleBrand(
    val id: Int,
    val name: String
)

data class VehicleModel(
    val id: Int,
    val brandId: Int,
    val name: String
)

data class VehicleYearRange(
    val id: Int,
    val modelId: Int,
    val fromYear: Int,
    val toYear: Int
) {
    val label: String
        get() = if (fromYear == toYear) fromYear.toString() else "$fromYear-$toYear"
}
