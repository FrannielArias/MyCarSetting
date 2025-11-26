package edu.ucne.loginapi.presentation.usuario

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.Usuarios

data class UsuarioUiState(
    val usuariosId: Int = 0,
    val userName: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val isSheetVisible: Boolean = false,
    val listaUsuarios: Resource<List<Usuarios>> = Resource.Loading(),
    val isLoggedIn: Boolean = false,
    val currentUser: Usuarios? = null
)