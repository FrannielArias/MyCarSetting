package edu.ucne.loginapi.data.remote.dataSource

import edu.ucne.loginapi.data.remote.apiService.UsuariosApiService
import edu.ucne.loginapi.data.remote.dto.UsuariosDto
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: UsuariosApiService
) {
    suspend fun getUsuarios(): List<UsuariosDto> {
        return api.getUsuarios()
    }

    suspend fun getUsuario(id: Int): List<UsuariosDto> {
        return api.getUsuario(id)
    }

    suspend fun saveUsuarios(usuariosDto: UsuariosDto) {
        return api.saveUsuarios(usuariosDto)
    }

    suspend fun updateUsuarios(usuariosDto: UsuariosDto) {
        return api.updateUsuarios(usuariosDto)
    }
}
