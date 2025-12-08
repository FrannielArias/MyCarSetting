package edu.ucne.loginapi.presentation.Services

import ServiceCategory
import ServicesUiState
import android.util.Log
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

    fun onEvent(event: ServicesEvent) {
        when (event) {
            ServicesEvent.LoadInitialData -> {}

            is ServicesEvent.LoadForLocation -> {
                loadServices(event.lat, event.lon, null)
            }

            is ServicesEvent.OnCategorySelected -> {
                _state.update { it.copy(selectedCategory = event.category) }
                val currentLat = _state.value.userLat
                val currentLon = _state.value.userLon
                if (currentLat != null && currentLon != null) {
                    loadServices(currentLat, currentLon, event.category)
                }
            }

            is ServicesEvent.OnServiceClicked -> {}

            ServicesEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun loadServices(lat: Double, lon: Double, category: ServiceCategory?) {
        viewModelScope.launch {
            Log.d("ServicesViewModel", "🔄 Iniciando búsqueda de servicios...")
            Log.d("ServicesViewModel", "📍 Ubicación: $lat, $lon")
            Log.d("ServicesViewModel", "🏷️ Categoría: $category")

            _state.update {
                it.copy(
                    isLoading = true,
                    userLat = lat,
                    userLon = lon
                )
            }

            val response = repository.searchServices(
                query = "",
                limit = 40,
                userLat = lat,
                userLon = lon,
                category = category
            )

            when (response) {
                is Resource.Success -> {
                    Log.d("ServicesViewModel", "✅ Servicios encontrados: ${response.data?.size ?: 0}")
                    response.data?.forEach { service ->
                        Log.d("ServicesViewModel", "  - ${service.name} (${service.distanceText})")
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            services = response.data ?: emptyList()
                        )
                    }
                }

                is Resource.Error -> {
                    Log.e("ServicesViewModel", "❌ Error: ${response.message}")
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
