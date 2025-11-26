package edu.ucne.loginapi.data.remote.dataSource

import edu.ucne.loginapi.data.mapper.toCreateRequest
import edu.ucne.loginapi.data.mapper.toDomain
import edu.ucne.loginapi.data.mapper.toUpdateRequest
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.apiService.CarApiService
import edu.ucne.loginapi.domain.model.UserCar
import javax.inject.Inject

class CarRemoteDataSource @Inject constructor(
    private val api: CarApiService
) {
    suspend fun getCars(): Resource<List<UserCar>> {
        return try {
            val response = api.getCars()
            if (response.isSuccessful) {
                val body = response.body().orEmpty()
                Resource.Success(body.map { it.toDomain() })
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun createCar(car: UserCar): Resource<UserCar> {
        return try {
            val response = api.createCar(car.toCreateRequest())
            if (response.isSuccessful) {
                val body = response.body() ?: return Resource.Error("Empty response")
                Resource.Success(body.toDomain())
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun updateCar(car: UserCar): Resource<UserCar> {
        return try {
            val response = api.updateCar(car.id, car.toUpdateRequest())
            if (response.isSuccessful) {
                val body = response.body() ?: return Resource.Error("Empty response")
                Resource.Success(body.toDomain())
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun deleteCar(id: String): Resource<Unit> {
        return try {
            val response = api.deleteCar(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun setCurrent(id: String): Resource<Unit> {
        return try {
            val response = api.setCurrent(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }
}