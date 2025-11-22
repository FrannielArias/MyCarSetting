package edu.ucne.loginapi.domain.model

import java.util.UUID

data class MaintenanceHistory(
    val id: String = UUID.randomUUID().toString(),
    val carId: String,
    val taskType: MaintenanceType,
    val serviceDateMillis: Long,
    val mileageKm: Int?,
    val cost: Double?,
    val workshopName: String?,
    val notes: String?
)
