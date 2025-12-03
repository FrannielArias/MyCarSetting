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
        private const val NETWORK_ERROR = "Network error"
        private const val EMPTY_RESPONSE = "Empty response"
    }

    suspend fun getTasksForCar(carId: Int): Resource<List<MaintenanceTask>> {
        return safeCall(
            call = { api.getTasksForCar(carId) },
            map = { list: List<MaintenanceTaskDto> -> list.map { it.toDomain() } }
        )
    }

    suspend fun createTask(task: MaintenanceTask): Resource<MaintenanceTask> {
        return safeCall(
            call = { api.createTask(task.toCreateRequest()) },
            map = { dto: MaintenanceTaskDto -> dto.toDomain() }
        )
    }

    suspend fun updateTask(task: MaintenanceTask): Resource<MaintenanceTask> {
        return safeCall(
            call = { api.updateTask(task.remoteId ?: task.id, task.toUpdateRequest()) },
            map = { dto: MaintenanceTaskDto -> dto.toDomain() }
        )
    }

    suspend fun deleteTask(id: Int): Resource<Unit> {
        return safeCallSimple { api.deleteTask(id) }
    }

    suspend fun getHistoryForCar(carId: Int): Resource<List<MaintenanceHistory>> {
        return safeCall(
            call = { api.getHistoryForCar(carId) },
            map = { list: List<MaintenanceHistoryDto> -> list.map { it.toDomain() } }
        )
    }

    suspend fun createHistory(history: MaintenanceHistory): Resource<MaintenanceHistory> {
        return safeCall(
            call = { api.createHistory(history.toCreateRequest()) },
            map = { dto: MaintenanceHistoryDto -> dto.toDomain() }
        )
    }

    suspend fun deleteHistory(id: Int): Resource<Unit> {
        return safeCallSimple { api.deleteHistory(id) }
    }

    private inline fun <reified T, R> safeCall(
        call: () -> retrofit2.Response<T>,
        map: (T) -> R
    ): Resource<R> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body() ?: return Resource.Error(EMPTY_RESPONSE)
                Resource.Success(map(body))
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }

    private inline fun safeCallSimple(
        call: () -> retrofit2.Response<Unit>
    ): Resource<Unit> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR)
        }
    }
}
