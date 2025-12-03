package edu.ucne.loginapi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.syncWorker.TriggerFullSyncUseCase
import edu.ucne.loginapi.domain.useCase.GetSessionUseCase
import edu.ucne.loginapi.domain.useCase.SchedulePeriodicSyncUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase,
    private val triggerFullSyncUseCase: TriggerFullSyncUseCase,
    private val schedulePeriodicSyncUseCase: SchedulePeriodicSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val session = getSessionUseCase().first()

            if (session != null) {
                triggerFullSyncUseCase()
                schedulePeriodicSyncUseCase()
            }

            _state.value = SplashUiState(
                isCheckingSession = false,
                isLoggedIn = session != null
            )
        }
    }
}
