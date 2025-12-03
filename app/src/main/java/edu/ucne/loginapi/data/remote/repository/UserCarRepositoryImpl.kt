package edu.ucne.loginapi.data.remote.repository

import edu.ucne.loginapi.data.dao.UserCarDao
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toEntity
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.UserCarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserCarRepositoryImpl @Inject constructor(
    private val dao: UserCarDao
) : UserCarRepository {

    override fun observeCurrentCar(): Flow<UserCar?> =
        dao.getCars().map { list ->
            list.firstOrNull { it.isCurrent }?.toDomain()
        }

    override fun observeCars(): Flow<List<UserCar>> =
        dao.getCars().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getCurrentCar(): UserCar? =
        dao.getCurrentCar()?.toDomain()

    override suspend fun getCarById(id: Int): UserCar? =
        dao.getCar(id)?.toDomain()

    override suspend fun upsertCar(car: UserCar): Resource<UserCar> {
        return try {
            dao.upsert(
                car.toEntity().copy(
                    pendingSync = true
                )
            )
            Resource.Success(car)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al guardar", car)
        }
    }

    override suspend fun setCurrentCar(id: Int): Resource<Unit> {
        return try {
            dao.clearCurrent()
            dao.setCurrent(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cambiar vehículo actual")
        }
    }

    override suspend fun deleteCar(id: Int): Resource<Unit> {
        return try {
            dao.delete(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al eliminar")
        }
    }
}