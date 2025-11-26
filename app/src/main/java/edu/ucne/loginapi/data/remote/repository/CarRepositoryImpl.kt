package edu.ucne.loginapi.data.repository

import edu.ucne.loginapi.data.dao.UserCarDao
import edu.ucne.loginapi.data.remote.dataSource.CarRemoteDataSource
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toEntity
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.CarRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CarRepositoryImpl @Inject constructor(
    private val userCarDao: UserCarDao,
    private val remote: CarRemoteDataSource
) : CarRepository {

    override fun getCars(): Flow<List<UserCar>> =
        userCarDao.getCars().map { list -> list.map { it.toDomain() } }

    override suspend fun getCar(id: String): UserCar? =
        userCarDao.getCar(id)?.toDomain()

    override suspend fun addCar(car: UserCar): Resource<Unit> {
        userCarDao.upsert(car.toEntity())
        return Resource.Success(Unit)
    }

    override suspend fun updateCar(car: UserCar): Resource<Unit> {
        userCarDao.upsert(car.toEntity())
        return Resource.Success(Unit)
    }

    override suspend fun deleteCar(id: String): Resource<Unit> {
        userCarDao.delete(id)
        return Resource.Success(Unit)
    }

    override suspend fun setCurrentCar(id: String): Resource<Unit> {
        userCarDao.clearCurrent()
        userCarDao.setCurrent(id)
        return Resource.Success(Unit)
    }

    override suspend fun syncCars(): Resource<Unit> {
        val result = remote.getCars()
        return when (result) {
            is Resource.Success -> {
                val cars = result.data.orEmpty()
                userCarDao.replaceAll(cars.map { it.toEntity() })
                Resource.Success(Unit)
            }
            is Resource.Error -> Resource.Error(result.message ?: "Error al sincronizar vehículos")
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun pushPendingCars(): Resource<Unit> {
        return Resource.Success(Unit)
    }
}
