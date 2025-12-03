package edu.ucne.loginapi.presentation.usuario

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.syncWorker.TriggerFullSyncUseCase
import edu.ucne.loginapi.domain.model.SessionInfo
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.useCase.ClearSessionUseCase
import edu.ucne.loginapi.domain.useCase.GetSessionUseCase
import edu.ucne.loginapi.domain.useCase.LoginUseCase
import edu.ucne.loginapi.domain.useCase.SaveSessionUseCase
import edu.ucne.loginapi.domain.useCase.SchedulePeriodicSyncUseCase
import edu.ucne.loginapi.domain.useCase.Usuarios.GetUsuariosUseCase
import edu.ucne.loginapi.domain.useCase.Usuarios.SaveUsuariosUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(
    private val getUsuariosUseCase: GetUsuariosUseCase,
    private val saveUsuariosUseCase: SaveUsuariosUseCase,
    private val saveSessionUseCase: SaveSessionUseCase,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val loginUseCase: LoginUseCase,
    private val triggerFullSyncUseCase: TriggerFullSyncUseCase,
    private val schedulePeriodicSyncUseCase: SchedulePeriodicSyncUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState(isLoading = true))
    val state: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    private var usuariosJob: Job? = null

    init {
        observarSesion()
        obtenerUsuarios()
    }

    private fun observarSesion() {
        viewModelScope.launch {
            getSessionUseCase().collect { session ->
                _uiState.update {
                    it.copy(
                        isLoggedIn = session.isLoggedIn,
                        currentUser = if (session.isLoggedIn && session.userId != null && session.userName != null) {
                            Usuarios(
                                usuarioId = session.userId,
                                userName = session.userName,
                                password = ""
                            )
                        } else null
                    )
                }
            }
        }
    }

    private fun obtenerUsuarios() {
        usuariosJob?.cancel()

        usuariosJob = viewModelScope.launch {
            getUsuariosUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                error = null,
                                message = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                listaUsuarios = result,
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: UsuarioEvent) {
        when (event) {
            is UsuarioEvent.Crear -> crearUsuario(event.usuarios)
            is UsuarioEvent.Login -> login()
            is UsuarioEvent.Logout -> logout()
            is UsuarioEvent.ShowBottonSheet -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = true,
                        isLoading = false,
                        error = null,
                        message = null
                    )
                }
            }

            is UsuarioEvent.HideBottonSheet -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = false,
                        isLoading = false,
                        userName = "",
                        password = "",
                        error = null,
                        message = null
                    )
                }
            }

            is UsuarioEvent.UserNameChange -> {
                _uiState.update { it.copy(userName = event.value, error = null) }
            }

            is UsuarioEvent.PasswordChange -> {
                _uiState.update { it.copy(password = event.value, error = null) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            clearSessionUseCase()
            _uiState.update {
                it.copy(
                    isLoggedIn = false,
                    currentUser = null,
                    userName = "",
                    password = "",
                    error = null,
                    message = null
                )
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            try {
                val userName = _uiState.value.userName.trim()
                val password = _uiState.value.password.trim()

                if (userName.isEmpty() || password.isEmpty()) {
                    _uiState.update {
                        it.copy(error = "Por favor complete todos los campos")
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true, error = null, message = null) }

                val result = loginUseCase(userName, password)

                when (result) {
                    is Resource.Success -> {
                        val usuario = result.data
                        if (usuario != null) {
                            saveSessionUseCase(
                                SessionInfo(
                                    isLoggedIn = true,
                                    userId = usuario.usuarioId,
                                    userName = usuario.userName
                                )
                            )

                            // 🔹 Sync completo inmediato
                            triggerFullSyncUseCase()

                            // 🔹 Programar sync periódico en background
                            schedulePeriodicSyncUseCase()

                            _uiState.update {
                                it.copy(
                                    isLoggedIn = true,
                                    currentUser = usuario,
                                    error = null,
                                    message = null,
                                    isLoading = false,
                                    password = ""
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    error = "Error al iniciar sesión",
                                    isLoading = false
                                )
                            }
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                error = result.message ?: "Error al iniciar sesión",
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error en login: ${e.message}")
                _uiState.update {
                    it.copy(
                        error = "Error al iniciar sesión: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun crearUsuario(usuario: Usuarios) {
        viewModelScope.launch {
            try {
                Log.d("UsuarioViewModel", "Intentando crear usuario: $usuario")

                if (usuario.userName.isNullOrBlank() || usuario.password.isNullOrBlank()) {
                    _uiState.update {
                        it.copy(
                            error = "Por favor complete todos los campos",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true, error = null) }

                val listaUsuarios = _uiState.value.listaUsuarios?.data ?: emptyList()
                val usuarioExiste = listaUsuarios.any {
                    it.userName.trim() == usuario.userName.trim()
                }

                if (usuarioExiste) {
                    _uiState.update {
                        it.copy(
                            error = "Este nombre de usuario ya existe",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                val result = saveUsuariosUseCase(usuario)

                when (result) {
                    is Resource.Success -> {
                        Log.d("UsuarioViewModel", "Usuario creado exitosamente")
                        _uiState.update {
                            it.copy(
                                message = "Usuario creado correctamente. Ahora puedes iniciar sesión",
                                isSheetVisible = false,
                                userName = "",
                                password = "",
                                error = null,
                                isLoading = false
                            )
                        }
                        obtenerUsuarios()
                    }

                    is Resource.Error -> {
                        Log.e("UsuarioViewModel", "Error: ${result.message}")
                        _uiState.update {
                            it.copy(
                                error = result.message ?: "Error al crear usuario",
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }

            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Exception: ${e.message}")
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error al crear usuario",
                        isLoading = false
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        usuariosJob?.cancel()
    }
}