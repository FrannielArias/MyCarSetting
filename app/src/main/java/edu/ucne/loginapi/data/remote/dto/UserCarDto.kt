package edu.ucne.loginapi.data.remote.dto

data class UserCarDto(
    val id: Int,
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String?,
    val fuelType: String,
    val usageType: String,
    val isCurrent: Boolean,
    val remoteId: Long? = null
)

data class CreateUserCarRequest(
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String?,
    val fuelType: String,
    val usageType: String
)

data class UpdateUserCarRequest(
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String?,
    val fuelType: String,
    val usageType: String,
    val isCurrent: Boolean
)