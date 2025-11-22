package edu.ucne.loginapi.data.repository

import edu.ucne.loginapi.data.UserCarDao
import edu.ucne.loginapi.data.local.dao.UserCarDao
import edu.ucne.loginapi.data.mapper.toDomain
import edu.ucne.loginapi.data.mapper.toEntity
import edu.ucne.loginapi.data.remote.CarRemoteDataSource
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.toDomain
import edu.ucne.loginapi.data.toEntity
import edu.ucne.loginapi.domain.model.Resource
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.CarRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CarRepositoryImpl @Inject constructor(
    private val userCarDao: UserCarDao,
    private val remote: CarRemoteDataSource
) : CarRepository {

    override fun observeCars(): Flow<List<UserCar>> =
        userCarDao.observeCars().map { list -> list.map { it.toDomain() } }

    override suspend fun getCurrentCar(): UserCar? =
        userCarDao.getCurrentCar()?.toDomain()

    override suspend fun addCar(car: UserCar): Resource<Unit> {
        val local = car.copy(isCurrent = true)
        userCarDao.clearCurrent()
        userCarDao.upsert(local.toEntity())
        return Resource.Success(Unit)
    }

    override suspend fun setCurrentCar(carId: String): Resource<Unit> {
        userCarDao.clearCurrent()
        userCarDao.setCurrent(carId)
        return Resource.Success(Unit)
    }

    override suspend fun deleteCar(carId: String): Resource<Unit> {
        userCarDao.delete(carId)
        return Resource.Success(Unit)
    }

    override suspend fun syncCars(): Resource<Unit> {
        val localResult = remote.getCars()
        return when (localResult) {
            is Resource.Success -> {
                val cars = localResult.data.orEmpty()
                userCarDao.replaceAll(cars.map { it.toEntity() })
                Resource.Success(Unit)
            }
            is Resource.Error -> localResult
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun pushPendingCars(): Resource<Unit> {
        val pendingCreates = userCarDao.getPendingCreates()
        val pendingUpdates = userCarDao.getPendingUpdates()
        val pendingDeletes = userCarDao.getPendingDeletes()

        for (entity in pendingCreates) {
            val domain = entity.toDomain()
            when (val result = remote.createCar(domain)) {
                is Resource.Success -> {
                    val synced = result.data ?: continue
                    userCarDao.markAsSyncedCreate(entity.id, synced.id)
                }
                is Resource.Error -> return Resource.Error("Error sincronizando vehículos")
                else -> {}
            }
        }

        for (entity in pendingUpdates) {
            val domain = entity.toDomain()
            when (val result = remote.updateCar(domain)) {
                is Resource.Success -> {
                    userCarDao.markAsSyncedUpdate(entity.id)
                }
                is Resource.Error -> return Resource.Error("Error sincronizando vehículos")
                else -> {}
            }
        }

        for (entity in pendingDeletes) {
            val remoteId = entity.remoteId ?: continue
            when (val result = remote.deleteCar(remoteId)) {
                is Resource.Success -> {
                    userCarDao.finalDelete(entity.id)
                }
                is Resource.Error -> return Resource.Error("Error sincronizando vehículos")
                else -> {}
            }
        }

        return Resource.Success(Unit)
    }
}
