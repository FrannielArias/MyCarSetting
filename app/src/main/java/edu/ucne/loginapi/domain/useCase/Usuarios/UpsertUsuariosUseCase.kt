package edu.ucne.loginapi.domain.useCase.Usuarios

import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.repository.UsuariosRepository
import javax.inject.Inject

class UpsertUsuariosUseCase @Inject constructor(
    private val repository: UsuariosRepository
){
    suspend operator fun invoke(usuarios: Usuarios): Result<Unit>{
        val validacion = validateUserName(usuarios.userName)
        if(!validacion.isValid)
            return Result.failure(Exception(validacion.error))
        return runCatching { repository.updateUsuarios(usuarios) }
    }
}