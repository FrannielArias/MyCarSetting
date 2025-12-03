package edu.ucne.loginapi.data.remote.dataSource

import edu.ucne.loginapi.data.remote.CarApiService
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toDto
import edu.ucne.loginapi.domain.model.UserCar
import javax.inject.Inject

class CarRemoteDataSource @Inject constructor(
    private val api: CarApiService
) {
    companion object {
        private const val NETWORK_ERROR = "Error de conexión"
        private const val EMPTY_RESPONSE = "Respuesta vacía del servidor"
    }

    suspend fun getCars(): Resource<List<UserCar>> {
        return try {
            val response = api.getCars()
            if (response.isSuccessful) {
                val list = response.body().orEmpty().map { it.toDomain() }
                Resource.Success(list)
            } else {
                Resource.Error("Error HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }

    suspend fun getCar(id: Int): Resource<UserCar> {
        return try {
            val response = api.getCar(id)
            if (response.isSuccessful) {
                val body = response.body() ?: return Resource.Error(EMPTY_RESPONSE)
                Resource.Success(body.toDomain())
            } else {
                Resource.Error("Error HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }

    suspend fun getCurrentCar(): Resource<UserCar> {
        return try {
            val response = api.getCurrentCar()
            if (response.isSuccessful) {
                val body = response.body() ?: return Resource.Error("No hay vehículo actual")
                Resource.Success(body.toDomain())
            } else {
                if (response.code() == 404) {
                    Resource.Error("No hay vehículo actual configurado")
                } else {
                    Resource.Error("Error HTTP ${response.code()}: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }

    suspend fun createCar(car: UserCar): Resource<UserCar> {
        return try {
            val response = api.createCar(car.toDto())
            if (response.isSuccessful) {
                val body = response.body() ?: return Resource.Error(EMPTY_RESPONSE)
                Resource.Success(body.toDomain())
            } else {
                Resource.Error("Error HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }

    suspend fun updateCar(car: UserCar): Resource<Unit> {
        return try {
            val response = api.updateCar(car.id, car.toDto())
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Error HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }

    suspend fun deleteCar(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteCar(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Error HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }
}