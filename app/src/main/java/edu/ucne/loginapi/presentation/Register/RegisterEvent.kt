package edu.ucne.loginapi.presentation.Register

sealed interface RegisterEvent {
    data class UserNameChange(val value: String) : RegisterEvent
    data class PasswordChange(val value: String) : RegisterEvent
    data class ConfirmPasswordChange(val value: String) : RegisterEvent
    data object Submit : RegisterEvent
    data object MessageShown : RegisterEvent
    data object NavigationHandled : RegisterEvent
}
