package edu.ucne.loginapi.data.remote.api

import edu.ucne.loginapi.data.remote.dto.CreateUserCarRequest
import edu.ucne.loginapi.data.remote.dto.UpdateUserCarRequest
import edu.ucne.loginapi.data.remote.dto.UserCarDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

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
