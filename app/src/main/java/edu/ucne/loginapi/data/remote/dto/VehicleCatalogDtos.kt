package edu.ucne.loginapi.data.remote.dto

data class VehicleBrandDto(
    val id: String,
    val name: String
)

data class VehicleModelDto(
    val id: String,
    val brandId: String,
    val name: String
)

data class VehicleYearRangeDto(
    val id: String,
    val modelId: String,
    val fromYear: Int,
    val toYear: Int
)
