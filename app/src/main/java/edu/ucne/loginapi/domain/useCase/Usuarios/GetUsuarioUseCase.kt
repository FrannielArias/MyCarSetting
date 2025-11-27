package edu.ucne.loginapi.domain.useCase.Usuarios

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.repository.UsuariosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsuarioUseCase @Inject constructor(
    private val repository: UsuariosRepository
) {
    operator fun invoke(id: Int): Flow<Resource<List<Usuarios>>> {
        return repository.getUsuario(id)
    }
}
