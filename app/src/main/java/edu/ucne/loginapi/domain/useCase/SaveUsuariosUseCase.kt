package edu.ucne.loginapi.domain.useCase

import androidx.compose.foundation.pager.rememberPagerState
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.repository.UsuariosRepository
import javax.inject.Inject

class SaveUsuariosUseCase @Inject constructor(
    private val repository: UsuariosRepository
){
    suspend operator fun invoke(usuarios: Usuarios) {
        repository.saveUsuarios(usuarios)
    }
}