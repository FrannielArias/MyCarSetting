package edu.ucne.loginapi.data.remote.repository

import edu.ucne.loginapi.data.local.dao.UserCarDao
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.toDomain
import edu.ucne.loginapi.data.toEntity
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.UserCarRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserCarRepositoryImpl @Inject constructor(
    private val userCarDao: UserCarDao
) : UserCarRepository {

    override fun observeCurrentCar(): Flow<UserCar?> =
        userCarDao.getCars().map { list ->
            list.firstOrNull { it.isCurrent }?.toDomain()
        }

    override fun observeCars(): Flow<List<UserCar>> =
        userCarDao.getCars().map { list -> list.map { it.toDomain() } }

    override suspend fun getCurrentCar(): UserCar? {
        val cars = userCarDao.getCars().first()
        return cars.firstOrNull { it.isCurrent }?.toDomain()
    }

    override suspend fun getCarById(id: String): UserCar? =
        userCarDao.getCar(id)?.toDomain()

    override suspend fun upsertCar(car: UserCar): Resource<UserCar> {
        return try {
            userCarDao.upsert(car.toEntity())
            Resource.Success(car)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al guardar vehículo", car)
        }
    }

    override suspend fun setCurrentCar(id: String): Resource<Unit> {
        return try {
            userCarDao.clearCurrent()
            userCarDao.setCurrent(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cambiar vehículo actual")
        }
    }

    override suspend fun deleteCar(id: String): Resource<Unit> {
        return try {
            userCarDao.delete(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al eliminar vehículo")
        }
    }
}