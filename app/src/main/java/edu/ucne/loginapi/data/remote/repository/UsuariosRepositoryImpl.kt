package edu.ucne.loginapi.data.remote.repository

import android.util.Log
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.dataSource.RemoteDataSource
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toDto
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.repository.UsuariosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class UsuariosRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : UsuariosRepository {

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
                Log.d("UsuariosRepository", "Actualizado exitosamente")
                Resource.Success(Unit)
            } else {
                Log.d("UsuariosRepository", "Validando nombre de usuario único: ${usuarios.userName}")

                val existingUsuarios = remoteDataSource.getUsuarios()
                val userNameAlreadyExists = existingUsuarios.any { existing ->
                    existing.userName.trim().equals(usuarios.userName.trim(), ignoreCase = true)
                }

                if (userNameAlreadyExists) {
                    Log.d("UsuariosRepository", "Nombre de usuario ya existe en la API")
                    return Resource.Error("El nombre de usuario ya está en uso")
                }

                Log.d("UsuariosRepository", "Creando nuevo usuario")
                remoteDataSource.saveUsuarios(dto)
                Log.d("UsuariosRepository", "Creado exitosamente")
                Resource.Success(Unit)
            }
        } catch (e: HttpException) {
            Log.e("UsuariosRepository", "HTTP Error: ${e.code()} - ${e.message()}", e)
            val message = when (e.code()) {
                409 -> "El nombre de usuario ya está en uso"
                400 -> "Datos inválidos, verifica la información"
                else -> "Error de servidor: ${e.message()}"
            }
            Resource.Error(message)
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

    override suspend fun login(userName: String, password: String): Resource<Usuarios> {
        return try {
            val usuariosDto = remoteDataSource.getUsuarios()
            val usuarioDto = usuariosDto.find {
                it.userName.trim() == userName.trim() &&
                        it.password.trim() == password.trim()
            }
            if (usuarioDto != null) {
                Resource.Success(usuarioDto.toDomain())
            } else {
                Resource.Error("Usuario o contraseña incorrectos")
            }
        } catch (e: HttpException) {
            Resource.Error("Error de servidor: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Error de red o desconocido: ${e.localizedMessage}")
        }
    }
}
