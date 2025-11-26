package edu.ucne.loginapi.domain.useCase.Usuarios

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.repository.UsuariosRepository
import javax.inject.Inject

class SaveUsuariosUseCase @Inject constructor(
    private val repository: UsuariosRepository
){
    suspend operator fun invoke(usuarios: Usuarios): Resource<Unit> {
        return repository.saveUsuarios(usuarios)
    }
}