package edu.ucne.loginapi.data.remote.apiService

import edu.ucne.loginapi.data.remote.dto.CreateMaintenanceHistoryRequest
import edu.ucne.loginapi.data.remote.dto.CreateMaintenanceTaskRequest
import edu.ucne.loginapi.data.remote.dto.MaintenanceHistoryDto
import edu.ucne.loginapi.data.remote.dto.MaintenanceTaskDto
import edu.ucne.loginapi.data.remote.dto.UpdateMaintenanceTaskRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MaintenanceApiService {
    @GET("api/cars/{carId}/tasks")
    suspend fun getTasksForCar(
        @Path("carId") carId: String
    ): Response<List<MaintenanceTaskDto>>

    @POST("api/tasks")
    suspend fun createTask(
        @Body request: CreateMaintenanceTaskRequest
    ): Response<MaintenanceTaskDto>

    @PUT("api/tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body request: UpdateMaintenanceTaskRequest
    ): Response<MaintenanceTaskDto>

    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(
        @Path("id") id: String
    ): Response<Unit>

    @GET("api/cars/{carId}/history")
    suspend fun getHistoryForCar(
        @Path("carId") carId: String
    ): Response<List<MaintenanceHistoryDto>>

    @POST("api/history")
    suspend fun createHistory(
        @Body request: CreateMaintenanceHistoryRequest
    ): Response<MaintenanceHistoryDto>

    @DELETE("api/history/{id}")
    suspend fun deleteHistory(
        @Path("id") id: String
    ): Response<Unit>
}
