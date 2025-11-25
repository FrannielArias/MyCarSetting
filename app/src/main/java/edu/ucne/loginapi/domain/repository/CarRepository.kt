package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.UserCar
import kotlinx.coroutines.flow.Flow

interface CarRepository {

    fun getCars(): Flow<List<UserCar>>

    suspend fun getCar(id: String): UserCar?

    suspend fun addCar(car: UserCar): Resource<Unit>

    suspend fun updateCar(car: UserCar): Resource<Unit>

    suspend fun deleteCar(id: String): Resource<Unit>

    suspend fun setCurrentCar(id: String): Resource<Unit>

    suspend fun syncCars(): Resource<Unit>

    suspend fun pushPendingCars(): Resource<Unit>
}
