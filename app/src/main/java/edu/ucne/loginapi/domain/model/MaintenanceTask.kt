package edu.ucne.loginapi.domain.model

import java.util.UUID

data class MaintenanceTask(
    val id: String = UUID.randomUUID().toString(),
    val remoteId: Long? = null,
    val carId: String,
    val type: MaintenanceType,
    val title: String,
    val description: String?,
    val dueDateMillis: Long?,
    val dueMileageKm: Int?,
    val severity: MaintenanceSeverity = MaintenanceSeverity.MEDIUM,
    val status: MaintenanceStatus,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val isPendingCreate: Boolean = false,
    val isPendingUpdate: Boolean = false,
    val isPendingDelete: Boolean = false
)

enum class MaintenanceType {
    OIL_CHANGE,
    FILTER,
    BRAKE_SERVICE,
    TIRE_ROTATION,
    TIRE_CHANGE,
    ALIGNMENT,
    BATTERY,
    COOLANT,
    INSURANCE_RENEWAL,
    TAX_RENEWAL,
    GENERAL_CHECK,
    OTHER
}

enum class MaintenanceStatus {
    UPCOMING,
    OVERDUE,
    COMPLETED
}

enum class MaintenanceSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
