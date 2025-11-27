package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.Usuarios
import kotlinx.coroutines.flow.Flow

interface UsuariosRepository {
    fun getUsuarios(): Flow<Resource<List<Usuarios>>>
    fun getUsuario(id: Int): Flow<Resource<List<Usuarios>>>
    suspend fun saveUsuarios(usuarios: Usuarios): Resource<Unit>
    suspend fun updateUsuarios(usuarios: Usuarios): Resource<Unit>
    suspend fun login(userName: String, password: String): Resource<Usuarios>
}
