package edu.ucne.loginapi.data.remote.dataSource

import edu.ucne.loginapi.data.remote.MaintenanceApiService
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.dto.MaintenanceHistoryDto
import edu.ucne.loginapi.data.remote.dto.MaintenanceTaskDto
import edu.ucne.loginapi.data.remote.mappers.toCreateRequest
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toUpdateRequest
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.model.MaintenanceTask
import javax.inject.Inject

class MaintenanceRemoteDataSource @Inject constructor(
    private val api: MaintenanceApiService
) {
    companion object {
        private const val NETWORK_ERROR_MESSAGE = "Network error"
        private const val EMPTY_RESPONSE_MESSAGE = "Empty response"
    }

    suspend fun getTasksForCar(carId: String): Resource<List<MaintenanceTask>> {
        return try {
            val response = api.getTasksForCar(carId)
            if (response.isSuccessful) {
                val body: List<MaintenanceTaskDto> = response.body().orEmpty()
                val tasks = body.map { dto: MaintenanceTaskDto -> dto.toDomain() }
                Resource.Success(tasks)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    suspend fun createTask(task: MaintenanceTask): Resource<MaintenanceTask> {
        return try {
            val response = api.createTask(task.toCreateRequest())
            if (response.isSuccessful) {
                val body: MaintenanceTaskDto = response.body()
                    ?: return Resource.Error(EMPTY_RESPONSE_MESSAGE)
                Resource.Success(body.toDomain())
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    suspend fun updateTask(task: MaintenanceTask): Resource<MaintenanceTask> {
        return try {
            val response = api.updateTask(task.id, task.toUpdateRequest())
            if (response.isSuccessful) {
                val body: MaintenanceTaskDto = response.body()
                    ?: return Resource.Error(EMPTY_RESPONSE_MESSAGE)
                Resource.Success(body.toDomain())
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    suspend fun deleteTask(id: String): Resource<Unit> {
        return try {
            val response = api.deleteTask(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    suspend fun getHistoryForCar(carId: String): Resource<List<MaintenanceHistory>> {
        return try {
            val response = api.getHistoryForCar(carId)
            if (response.isSuccessful) {
                val body: List<MaintenanceHistoryDto> = response.body().orEmpty()
                val history = body.map { dto: MaintenanceHistoryDto -> dto.toDomain() }
                Resource.Success(history)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    suspend fun createHistory(history: MaintenanceHistory): Resource<MaintenanceHistory> {
        return try {
            val response = api.createHistory(history.toCreateRequest())
            if (response.isSuccessful) {
                val body: MaintenanceHistoryDto = response.body()
                    ?: return Resource.Error(EMPTY_RESPONSE_MESSAGE)
                Resource.Success(body.toDomain())
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    suspend fun deleteHistory(id: String): Resource<Unit> {
        return try {
            val response = api.deleteHistory(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }
}