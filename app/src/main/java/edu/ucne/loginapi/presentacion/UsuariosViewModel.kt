package edu.ucne.loginapi.presentacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.useCase.GetUsuarioUseCase
import edu.ucne.loginapi.domain.useCase.GetUsuariosUseCase
import edu.ucne.loginapi.domain.useCase.SaveUsuariosUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuariosViewModel @Inject constructor(
    private val getUsuariosUseCase: GetUsuariosUseCase,
    private val getUsuarioUseCase: GetUsuarioUseCase,
    private val saveUsuariosUseCase: SaveUsuariosUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(UsuarioUiState(isLoading = true))
    val state: StateFlow<UsuarioUiState> = _state.asStateFlow()

    private var usuariosJob: Job? = null

    init {
        obtenerUsuarios()
    }

    private fun obtenerUsuarios() {
        usuariosJob?.cancel()

        usuariosJob = viewModelScope.launch {
            getUsuariosUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                error = null,
                                message = null
                            )
                        }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                listaUsuarios = result,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
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

    private fun obtenerUsuario(id: Int) {
        viewModelScope.launch {
            getUsuarioUseCase(id).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val usuario = result.data?.firstOrNull()
                        usuario?.let { user ->
                            _state.update { state ->
                                state.copy(
                                    usuariosIs = user.usuarioId ?: 0,
                                    userName = user.userName ?: "",
                                    password = user.password ?: "",
                                    isLoading = false
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(error = result.message, isLoading = false)
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: UsuariosUiEvent) {
        when (event) {
            is UsuariosUiEvent.Crear -> crearUsuario(event.gastos)
            is UsuariosUiEvent.GetUsuarios -> obtenerUsuario(event.id)
            is UsuariosUiEvent.Load -> obtenerUsuarios()

            is UsuariosUiEvent.ShowBottonSheet -> {
                _state.update {
                    it.copy(
                        isSheetVisible = true,
                        isLoading = false
                    )
                }
            }

            is UsuariosUiEvent.HideBottonSheet -> {
                _state.update {
                    it.copy(
                        isSheetVisible = false,
                        isLoading = false,
                        usuariosIs = 0,
                        userName = "",
                        password = ""
                    )
                }
            }

            is UsuariosUiEvent.UserNameChange -> {
                _state.update { it.copy(userName = event.value) }
            }

            is UsuariosUiEvent.PasswordChange -> {
                _state.update { it.copy(password = event.value) }
            }
        }
    }

    private fun crearUsuario(usuario: Usuarios) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                saveUsuariosUseCase(usuario)

                _state.update {
                    it.copy(
                        message = "Usuario creado correctamente",
                        isSheetVisible = false,
                        usuariosIs = 0,
                        userName = "",
                        password = "",
                        error = null,
                        isLoading = false
                    )
                }
                obtenerUsuarios()

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Error al guardar",
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