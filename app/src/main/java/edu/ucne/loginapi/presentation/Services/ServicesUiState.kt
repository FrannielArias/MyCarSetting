data class ServicesUiState(
    val isLoading: Boolean = true,
    val services: List<ServiceItem> = emptyList(),
    val selectedCategory: ServiceCategory? = null,
    val userMessage: String? = null
)

data class ServiceItem(
    val id: String,
    val name: String,
    val category: ServiceCategory,
    val description: String,
    val distanceText: String,
    val isOpen: Boolean,
    val latitude: Double,
    val longitude: Double
)

enum class ServiceCategory {
    TALLER,
    MANTENIMIENTO,
    LAVADO,
    EMERGENCIA
}