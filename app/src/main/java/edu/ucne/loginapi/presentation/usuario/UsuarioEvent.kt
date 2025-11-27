package edu.ucne.loginapi.presentation.usuario

import edu.ucne.loginapi.domain.model.Usuarios

interface UsuarioEvent {
    object ShowBottonSheet : UsuarioEvent
    object HideBottonSheet : UsuarioEvent
    data object Load : UsuarioEvent
    data class Crear(val usuarios: Usuarios) : UsuarioEvent
    data class GetUsuarios(val id: Int) : UsuarioEvent
    data class UserNameChange(val value: String): UsuarioEvent
    data class PasswordChange(val value: String): UsuarioEvent
    data object Login : UsuarioEvent
    data object Logout : UsuarioEvent
}
