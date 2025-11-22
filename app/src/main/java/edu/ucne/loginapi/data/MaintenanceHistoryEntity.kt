package edu.ucne.loginapi.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_history")
data class MaintenanceHistoryEntity(
    @PrimaryKey val id: String,
    val carId: String,
    val taskType: String,
    val serviceDateMillis: Long,
    val mileageKm: Int?,
    val cost: Double?,
    val workshopName: String?,
    val notes: String?
)
