package edu.ucne.loginapi.presentacion

import edu.ucne.loginapi.domain.model.Usuarios

interface UsuariosUiEvent {
    object ShowBottonSheet : UsuariosUiEvent
    object HideBottonSheet : UsuariosUiEvent
    data object Load : UsuariosUiEvent
    data class Crear(val gastos: Usuarios) : UsuariosUiEvent
    data class GetUsuarios(val id: Int) : UsuariosUiEvent
    data class UserNameChange(val value: String): UsuariosUiEvent
    data class PasswordChange(val value: String): UsuariosUiEvent
}