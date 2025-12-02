package edu.ucne.loginapi.presentation.Register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.useCase.Usuarios.SaveUsuariosUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val saveUsuariosUseCase: SaveUsuariosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UserNameChange -> {
                _state.update { it.copy(userName = event.value, error = null, message = null) }
            }

            is RegisterEvent.PasswordChange -> {
                _state.update { it.copy(password = event.value, error = null, message = null) }
            }

            is RegisterEvent.ConfirmPasswordChange -> {
                _state.update {
                    it.copy(
                        confirmPassword = event.value,
                        error = null,
                        message = null
                    )
                }
            }

            RegisterEvent.Submit -> submit()
            RegisterEvent.MessageShown -> {
                _state.update { it.copy(message = null, error = null) }
            }

            RegisterEvent.NavigationHandled -> {
                _state.update { it.copy(navigateBack = false) }
            }
        }
    }

    private fun submit() {
        val current = _state.value
        val userName = current.userName.trim()
        val password = current.password.trim()
        val confirmPassword = current.confirmPassword.trim()

        if (userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _state.update { it.copy(error = "Por favor complete todos los campos") }
            return
        }

        if (password.length < 6) {
            _state.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        if (password != confirmPassword) {
            _state.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, message = null) }

            val usuario = Usuarios(
                usuarioId = null,
                userName = userName,
                password = password
            )

            when (val result = saveUsuariosUseCase(usuario)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            message = "Usuario creado correctamente. Ahora puedes iniciar sesión",
                            userName = "",
                            password = "",
                            confirmPassword = "",
                            navigateBack = true
                        )
                    }
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Error al crear el usuario"
                        )
                    }
                }

                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}