package edu.ucne.loginapi.data.remote.apiService

import edu.ucne.loginapi.data.remote.dto.UsuariosDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
