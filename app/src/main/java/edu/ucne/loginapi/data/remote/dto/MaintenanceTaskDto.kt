package edu.ucne.loginapi.data.remote.dto

data class MaintenanceTaskDto(
    val id: String,
    val carId: String,
    val type: String,
    val title: String,
    val description: String?,
    val dueDateMillis: Long?,
    val dueMileageKm: Int?,
    val status: String,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

data class CreateMaintenanceTaskRequest(
    val carId: String,
    val type: String,
    val title: String,
    val description: String?,
    val dueDateMillis: Long?,
    val dueMileageKm: Int?
)

data class UpdateMaintenanceTaskRequest(
    val type: String,
    val title: String,
    val description: String?,
    val dueDateMillis: Long?,
    val dueMileageKm: Int?,
    val status: String
)
