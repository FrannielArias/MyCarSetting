package edu.ucne.loginapi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_cars")
data class UserCarEntity(
    @PrimaryKey val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String?,
    val fuelType: String,
    val usageType: String,
    val isCurrent: Boolean,
    val remoteId: Long?
)
