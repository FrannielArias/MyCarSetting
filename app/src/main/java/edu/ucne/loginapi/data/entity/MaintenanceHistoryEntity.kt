package edu.ucne.loginapi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_history")
data class MaintenanceHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carId: Int,
    val taskType: String,
    val serviceDateMillis: Long,
    val mileageKm: Int?,
    val workshopName: String?,
    val cost: Double?,
    val notes: String?
)
