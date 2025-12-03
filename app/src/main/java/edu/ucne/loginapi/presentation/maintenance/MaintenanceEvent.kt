package edu.ucne.loginapi.presentation.maintenance

import edu.ucne.loginapi.domain.model.MaintenanceSeverity
import edu.ucne.loginapi.domain.model.MaintenanceTask

sealed interface MaintenanceEvent {
    object LoadInitialData : MaintenanceEvent
    object Refresh : MaintenanceEvent
    object ShowCreateSheet : MaintenanceEvent
    object HideCreateSheet : MaintenanceEvent

    data class OnNewTitleChange(val value: String) : MaintenanceEvent
    data class OnNewDescriptionChange(val value: String) : MaintenanceEvent
    data class OnNewDueMileageChange(val value: String) : MaintenanceEvent
    data class OnNewDueDateSelected(val millis: Long, val formatted: String) : MaintenanceEvent
    object OnClearNewDueDate : MaintenanceEvent

    data class OnNewSeveritySelected(val severity: MaintenanceSeverity) : MaintenanceEvent

    data class OnCompleteTask(val taskId: Int) : MaintenanceEvent
    data class OnDeleteTask(val taskId: Int) : MaintenanceEvent
    data class OnTaskClicked(val task: MaintenanceTask) : MaintenanceEvent

    object OnUserMessageShown : MaintenanceEvent
}
