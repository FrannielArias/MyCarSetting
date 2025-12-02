package edu.ucne.loginapi.data.remote.dto

data class VehicleBrandDto(
    val id: Int = 0,
    val name: String
)

data class VehicleModelDto(
    val id: Int = 0,
    val brandId: Int,
    val name: String
)

data class VehicleYearRangeDto(
    val id: Int = 0,
    val modelId: Int,
    val fromYear: Int,
    val toYear: Int
)
