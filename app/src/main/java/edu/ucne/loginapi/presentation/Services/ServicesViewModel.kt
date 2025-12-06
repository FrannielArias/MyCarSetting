package edu.ucne.loginapi.presentation.Services

import ServiceCategory
import ServiceItem
import ServicesUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.domain.repository.ServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val repository: ServicesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServicesUiState())
    val state = _state.asStateFlow()

    init {
        onEvent(ServicesEvent.LoadInitialData)
    }

    fun onEvent(event: ServicesEvent) {
        when (event) {
            ServicesEvent.LoadInitialData -> loadServices()
            is ServicesEvent.OnCategorySelected ->
                _state.update { it.copy(selectedCategory = event.category) }

            is ServicesEvent.OnServiceClicked ->
                _state.update { it.copy(userMessage = "Seleccionaste ${event.id}") }

            ServicesEvent.OnUserMessageShown ->
                _state.update { it.copy(userMessage = null) }
        }
    }

    private fun loadServices() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = repository.searchServices(
                query = "car repair",
                limit = 20,
                userLat = null,
                userLon = null,
                category = null
            )

            when (result) {
                is edu.ucne.loginapi.data.remote.Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            services = result.data ?: emptyList()
                        )
                    }
                }
                is edu.ucne.loginapi.data.remote.Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            userMessage = "Error: ${result.message}"
                        )
                    }
                }

                else -> {}
            }
        }
    }
}
