package edu.ucne.loginapi.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_tasks")
data class MaintenanceTaskEntity(
    @PrimaryKey val id: String,
    val remoteId: Long?,
    val carId: String,
    val type: String,
    val title: String,
    val description: String?,
    val dueDateMillis: Long?,
    val dueMileageKm: Int?,
    val status: String,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val isPendingCreate: Boolean,
    val isPendingUpdate: Boolean,
    val isPendingDelete: Boolean
)
