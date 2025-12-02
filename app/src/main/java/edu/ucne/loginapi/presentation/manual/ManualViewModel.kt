package edu.ucne.loginapi.presentation.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.domain.useCase.guideArticle.GetGuideArticleDetailUseCase
import edu.ucne.loginapi.domain.useCase.guideArticle.GetGuideArticlesUseCase
import edu.ucne.loginapi.domain.useCase.warningLights.GetWarningLightDetailUseCase
import edu.ucne.loginapi.domain.useCase.warningLights.GetWarningLightsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManualViewModel @Inject constructor(
    private val getWarningLightsUseCase: GetWarningLightsUseCase,
    private val getWarningLightDetailUseCase: GetWarningLightDetailUseCase,
    private val getGuideArticlesUseCase: GetGuideArticlesUseCase,
    private val getGuideArticleDetailUseCase: GetGuideArticleDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ManualUiState())
    val state: StateFlow<ManualUiState> = _state.asStateFlow()

    init {
        onEvent(ManualEvent.LoadInitialData)
    }

    fun onEvent(event: ManualEvent) {
        when (event) {
            ManualEvent.LoadInitialData -> loadInitial()
            is ManualEvent.SelectTab -> {
                _state.update { it.copy(selectedTabIndex = event.index) }
            }
            is ManualEvent.OnWarningLightClicked -> loadWarningDetail(event.id)
            is ManualEvent.OnGuideClicked -> loadGuideDetail(event.id)
            ManualEvent.OnDismissDetail -> {
                _state.update {
                    it.copy(
                        selectedWarningLight = null,
                        selectedArticle = null
                    )
                }
            }
            ManualEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, userMessage = null) }

            try {
                val lights = getWarningLightsUseCase().first()
                val guides = getGuideArticlesUseCase(null).first()

                _state.update {
                    it.copy(
                        warningLights = lights,
                        guideArticles = guides,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        userMessage = e.message ?: "Error al cargar el manual"
                    )
                }
            }
        }
    }

    private fun loadWarningDetail(id: Int) {
        viewModelScope.launch {
            try {
                val detail = getWarningLightDetailUseCase(id).first()
                _state.update {
                    it.copy(
                        selectedWarningLight = detail,
                        selectedArticle = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        userMessage = e.message ?: "Error al cargar el detalle del testigo"
                    )
                }
            }
        }
    }

    private fun loadGuideDetail(id: Int) {  // ← Int
        viewModelScope.launch {
            try {
                val detail = getGuideArticleDetailUseCase(id).first()
                _state.update {
                    it.copy(
                        selectedArticle = detail,
                        selectedWarningLight = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        userMessage = e.message ?: "Error al cargar la guía"
                    )
                }
            }
        }
    }
}