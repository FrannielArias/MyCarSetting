package edu.ucne.loginapi.data.remote.dto

data class MaintenanceHistoryDto(
    val id: Int,
    val carId: Int,
    val taskType: String,
    val serviceDateMillis: Long,
    val mileageKm: Int?,
    val workshopName: String?,
    val cost: Double?,
    val notes: String?
)

data class CreateMaintenanceHistoryRequest(
    val carId: Int,
    val taskType: String,
    val serviceDateMillis: Long,
    val mileageKm: Int?,
    val workshopName: String?,
    val cost: Double?,
    val notes: String?
)
