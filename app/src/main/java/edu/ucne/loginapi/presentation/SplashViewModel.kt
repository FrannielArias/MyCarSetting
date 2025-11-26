package edu.ucne.loginapi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.domain.useCase.GetSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<AppDestination?>(null)
    val startDestination: StateFlow<AppDestination?> = _startDestination

    init {
        viewModelScope.launch {
            val session = getSessionUseCase().first()
            _startDestination.value = if (session.isLoggedIn) {
                AppDestination.Dashboard
            } else {
                AppDestination.Login
            }
        }
    }
}
