package edu.ucne.loginapi.data.remote

import edu.ucne.loginapi.data.remote.dto.ChatRequestDto
import edu.ucne.loginapi.data.remote.dto.ChatResponseDto
import edu.ucne.loginapi.data.remote.dto.CreateMaintenanceHistoryRequest
import edu.ucne.loginapi.data.remote.dto.CreateMaintenanceTaskRequest
import edu.ucne.loginapi.data.remote.dto.CreateUserCarRequest
import edu.ucne.loginapi.data.remote.dto.GuideArticleDto
import edu.ucne.loginapi.data.remote.dto.MaintenanceHistoryDto
import edu.ucne.loginapi.data.remote.dto.MaintenanceTaskDto
import edu.ucne.loginapi.data.remote.dto.UpdateMaintenanceTaskRequest
import edu.ucne.loginapi.data.remote.dto.UpdateUserCarRequest
import edu.ucne.loginapi.data.remote.dto.UserCarDto
import edu.ucne.loginapi.data.remote.dto.UsuariosDto
import edu.ucne.loginapi.data.remote.dto.WarningLightDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuariosApiService {
    @GET("api/Usuarios")
    suspend fun getUsuarios(): List<UsuariosDto>

    @GET("api/Usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): List<UsuariosDto>

    @POST("api/Usuarios")
    suspend fun saveUsuarios(@Body usuariosDto: UsuariosDto)

    @PUT("api/Usuarios/{id}")
    suspend fun updateUsuarios(@Body usuariosDto: UsuariosDto)
}

//Api Chat
interface ChatApi {
    @POST("chat")
    suspend fun sendMessage(
        @Body request: ChatRequestDto
    ): ChatResponseDto
}

//Api Car
interface CarApiService {
    @GET("api/cars")
    suspend fun getCars(): Response<List<UserCarDto>>

    @POST("api/cars")
    suspend fun createCar(@Body request: CreateUserCarRequest): Response<UserCarDto>

    @PATCH("api/cars/{id}")
    suspend fun updateCar(
        @Path("id") id: String,
        @Body request: UpdateUserCarRequest
    ): Response<UserCarDto>

    @DELETE("api/cars/{id}")
    suspend fun deleteCar(@Path("id") id: String): Response<Unit>

    @POST("api/cars/{id}/setCurrent")
    suspend fun setCurrent(@Path("id") id: String): Response<Unit>
}

//Api Maintenance
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

//Api Manual
interface ManualApiService {
    @GET("api/manual/warningLights")
    suspend fun getWarningLights(
        @Query("brand") brand: String?,
        @Query("model") model: String?,
        @Query("year") year: Int?
    ): Response<List<WarningLightDto>>

    @GET("api/manual/warningLights/{id}")
    suspend fun getWarningLightDetail(
        @Path("id") id: String
    ): Response<WarningLightDto>

    @GET("api/manual/guides")
    suspend fun getGuideArticles(
        @Query("category") category: String?
    ): Response<List<GuideArticleDto>>

    @GET("api/manual/guides/{id}")
    suspend fun getGuideArticleDetail(
        @Path("id") id: String
    ): Response<GuideArticleDto>
}
