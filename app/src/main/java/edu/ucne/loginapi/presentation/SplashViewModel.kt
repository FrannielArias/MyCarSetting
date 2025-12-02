package edu.ucne.loginapi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.domain.useCase.GetSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val session = getSessionUseCase()
            _state.value = SplashUiState(
                isCheckingSession = false,
                isLoggedIn = session != null
            )
        }
    }
}
