package edu.ucne.loginapi.domain.model

data class MaintenanceHistory(
    val id: Int = 0,
    val carId: Int,
    val taskType: MaintenanceType,
    val serviceDateMillis: Long,
    val mileageKm: Int?,
    val cost: Double?,
    val workshopName: String?,
    val notes: String?
)