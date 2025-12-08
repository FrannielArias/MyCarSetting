package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.UserCar
import kotlinx.coroutines.flow.Flow

interface UserCarRepository {
    fun observeCurrentCar(): Flow<UserCar?>
    fun observeCars(): Flow<List<UserCar>>
    suspend fun getCurrentCar(): UserCar?
    suspend fun getCarById(id: Int): UserCar?
    suspend fun upsertCar(car: UserCar): Resource<UserCar>
    suspend fun setCurrentCar(id: Int): Resource<Unit>
    suspend fun deleteCar(id: Int): Resource<Unit>

}
