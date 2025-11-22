package edu.ucne.loginapi.data.remote.dto

data class MaintenanceHistoryDto(
    val id: String,
    val carId: String,
    val taskType: String,
    val serviceDateMillis: Long,
    val mileageKm: Int?,
    val workshopName: String?,
    val cost: Double?,
    val notes: String?
)

data class CreateMaintenanceHistoryRequest(
    val carId: String,
    val taskType: String,
    val serviceDateMillis: Long,
    val mileageKm: Int?,
    val workshopName: String?,
    val cost: Double?,
    val notes: String?
)
