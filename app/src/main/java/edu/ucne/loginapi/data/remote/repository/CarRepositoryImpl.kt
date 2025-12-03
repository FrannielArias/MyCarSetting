package edu.ucne.loginapi.data.remote.repository

import edu.ucne.loginapi.data.dao.UserCarDao
import edu.ucne.loginapi.data.entity.UserCarEntity
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.dataSource.CarRemoteDataSource
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toEntity
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CarRepositoryImpl @Inject constructor(
    private val userCarDao: UserCarDao,
    private val remote: CarRemoteDataSource
) : CarRepository {

    override fun getCars(): Flow<List<UserCar>> =
        userCarDao.getCars().map { cars -> cars.map { it.toDomain() } }

    override suspend fun getCar(id: String): UserCar? {
        val intId = id.toIntOrNull() ?: return null
        return userCarDao.getCar(intId)?.toDomain()
    }

    override suspend fun addCar(car: UserCar): Resource<Unit> {
        return try {
            val existing = userCarDao.getCar(car.id)
            if (existing != null) {
                return Resource.Error("El vehículo ya existe")
            }

            userCarDao.upsert(
                car.toEntity().copy(
                    pendingSync = true
                )
            )
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al agregar vehículo")
        }
    }

    override suspend fun updateCar(car: UserCar): Resource<Unit> {
        return try {
            val existing = userCarDao.getCar(car.id)
            if (existing == null) {
                return Resource.Error("El vehículo no existe")
            }

            userCarDao.upsert(
                car.toEntity().copy(
                    pendingSync = true
                )
            )
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al actualizar vehículo")
        }
    }

    override suspend fun deleteCar(id: String): Resource<Unit> {
        return try {
            val intId = id.toIntOrNull() ?: return Resource.Error("ID inválido")
            userCarDao.delete(intId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al eliminar vehículo")
        }
    }

    override suspend fun setCurrentCar(id: String): Resource<Unit> {
        return try {
            val intId = id.toIntOrNull() ?: return Resource.Error("ID inválido")
            userCarDao.clearCurrent()
            userCarDao.setCurrent(intId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cambiar vehículo actual")
        }
    }

    override suspend fun syncCars(): Resource<Unit> {
        return try {
            when (val result = remote.getCars()) {
                is Resource.Success -> {
                    val remoteCars: List<UserCar> = result.data ?: emptyList()

                    val entities: List<UserCarEntity> = remoteCars.map { car ->
                        car.toEntity().copy(pendingSync = false)
                    }

                    userCarDao.replaceAll(entities)
                    Resource.Success(Unit)
                }

                is Resource.Error -> Resource.Error(
                    result.message ?: "Error al sincronizar vehículos"
                )

                is Resource.Loading -> Resource.Loading()
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de sincronización")
        }
    }

    override suspend fun pushPendingCars(): Resource<Unit> {
        return try {
            val pendingCars = userCarDao.getPendingSync()

            for (entity in pendingCars) {
                val car: UserCar = entity.toDomain()

                val response = remote.createCar(car)
                when (response) {
                    is Resource.Success -> {
                        val updatedRemote = response.data
                        if (updatedRemote != null) {
                            userCarDao.upsert(
                                updatedRemote
                                    .toEntity()
                                    .copy(pendingSync = false)
                            )
                        }
                    }

                    is Resource.Error -> {
                    }

                    is Resource.Loading -> Unit
                }
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al sincronizar cambios pendientes")
        }
    }
}