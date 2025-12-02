package edu.ucne.loginapi.domain.model

enum class VehicleAlertLevel {
    CRITICAL,
    IMPORTANT,
    RECOMMENDATION,
    INFO
}

data class VehicleAlert(
    val id: String,
    val level: VehicleAlertLevel,
    val title: String,
    val message: String,
    val relatedTaskId: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)
