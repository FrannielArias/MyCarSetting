package edu.ucne.loginapi.presentation.Services

import ServiceCategory
import ServiceItem
import ServicesUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
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
        loadServices(null)
    }

    fun onEvent(event: ServicesEvent) {
        when (event) {
            ServicesEvent.LoadInitialData -> loadServices(null)

            is ServicesEvent.OnCategorySelected -> {
                _state.update { it.copy(selectedCategory = event.category) }
                loadServices(event.category)
            }

            is ServicesEvent.OnServiceClicked -> {
                _state.update { it.copy(userMessage = "Seleccionaste ${event.id}") }
            }

            ServicesEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun loadServices(category: ServiceCategory?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val response =
                repository.searchServices(
                    query = "",
                    limit = 40,
                    userLat = 19.3035,   // Cotuí real
                    userLon = -70.2500,
                    category = category
                )

            when (response) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            services = response.data ?: emptyList()
                        )
                    }
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            userMessage = response.message
                        )
                    }
                }

                else -> {}
            }
        }
    }
}
