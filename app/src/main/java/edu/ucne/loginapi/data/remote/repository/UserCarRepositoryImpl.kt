package edu.ucne.loginapi.repository.data

import edu.ucne.loginapi.data.UserCarDao
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.toDomain
import edu.ucne.loginapi.data.toEntity
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.UserCarRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserCarRepositoryImpl @Inject constructor(
    private val userCarDao: UserCarDao
) : UserCarRepository {

    override fun observeCurrentCar(): Flow<UserCar?> {
        return userCarDao.observeCurrentCar().map { entity ->
            entity?.toDomain()
        }
    }

    override fun observeCars(): Flow<List<UserCar>> {
        return userCarDao.observeCars().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getCurrentCar(): UserCar? {
        return userCarDao.getCurrentCar()?.toDomain()
    }

    override suspend fun getCarById(id: String): UserCar? {
        return userCarDao.getCarById(id)?.toDomain()
    }

    override suspend fun upsertCar(car: UserCar): Resource<UserCar> {
        return try {
            if (car.isCurrent) {
                userCarDao.clearCurrentCar()
            }
            userCarDao.upsert(car.toEntity())
            if (car.isCurrent) {
                userCarDao.setCurrentCar(car.id)
            }
            Resource.Success(car)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al guardar vehículo", car)
        }
    }

    override suspend fun setCurrentCar(id: String): Resource<Unit> {
        return try {
            userCarDao.clearCurrentCar()
            userCarDao.setCurrentCar(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al cambiar vehículo actual")
        }
    }

    override suspend fun deleteCar(id: String): Resource<Unit> {
        return try {
            userCarDao.deleteCar(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al eliminar vehículo")
        }
    }
}
