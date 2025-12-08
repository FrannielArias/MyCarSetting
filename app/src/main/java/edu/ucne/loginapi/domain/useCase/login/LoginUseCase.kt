package edu.ucne.loginapi.domain.useCase.login

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.repository.UsuariosRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UsuariosRepository
) {
    suspend operator fun invoke(userName: String, password: String): Resource<Usuarios> {
        return repository.login(userName, password)
    }

}