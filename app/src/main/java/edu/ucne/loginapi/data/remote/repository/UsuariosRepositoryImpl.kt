package edu.ucne.loginapi.data.remote.repository

import android.util.Log
import retrofit2.HttpException
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toDto
import edu.ucne.loginapi.data.remote.dataSource.RemoteDataSource
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.repository.UsuariosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UsuariosRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
): UsuariosRepository {

    override fun getUsuario(id: Int): Flow<Resource<List<Usuarios>>> = flow {
        try {
            emit(Resource.Loading<List<Usuarios>>())
            val usuariosDto = remoteDataSource.getUsuario(id)
            val usuarios = usuariosDto.map { it.toDomain() }
            emit(Resource.Success(usuarios))
        } catch (e: HttpException) {
            emit(Resource.Error("Error de servidor: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Error desconocido: ${e.localizedMessage}"))
        }
    }

    override suspend fun saveUsuarios(usuarios: Usuarios): Resource<Unit> {
        return try {
            Log.d("UsuariosRepository", "Guardando: $usuarios")
            val dto = usuarios.toDto()
            Log.d("UsuariosRepository", "DTO: $dto")

            if (usuarios.usuarioId != null && usuarios.usuarioId != 0) {
                Log.d("UsuariosRepository", "Actualizando usuario con ID: ${usuarios.usuarioId}")
                remoteDataSource.updateUsuarios(dto)
            } else {
                Log.d("UsuariosRepository", "Creando nuevo usuario")
                remoteDataSource.saveUsuarios(dto)
            }

            Log.d("UsuariosRepository", "Guardado exitosamente")
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Log.e("UsuariosRepository", "HTTP Error: ${e.code()} - ${e.message()}")
            Resource.Error("Error de servidor: ${e.message}")
        } catch (e: Exception) {
            Log.e("UsuariosRepository", "Error: ${e.message}", e)
            Resource.Error("Error desconocido: ${e.localizedMessage}")
        }
    }

    override fun getUsuarios(): Flow<Resource<List<Usuarios>>> = flow {
        try {
            emit(Resource.Loading())
            val usuariosDto = remoteDataSource.getUsuarios()
            val usuarios = usuariosDto.map { it.toDomain() }
            emit(Resource.Success(usuarios))
        } catch (e: HttpException) {
            emit(Resource.Error("Error de servidor: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("Error desconocido: ${e.localizedMessage}"))
        }
    }

    override suspend fun updateUsuarios(usuarios: Usuarios): Resource<Unit> {
        return try {
            val usuariosDto = usuarios.toDto()
            remoteDataSource.updateUsuarios(usuariosDto)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Error("Error de servidor: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error desconocido: ${e.localizedMessage}")
        }
    }
}