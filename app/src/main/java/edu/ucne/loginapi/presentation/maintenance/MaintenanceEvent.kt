package edu.ucne.loginapi.presentation.maintenance

import edu.ucne.loginapi.domain.model.MaintenanceTask

sealed interface MaintenanceEvent {
    object LoadInitialData : MaintenanceEvent
    object Refresh : MaintenanceEvent
    object ShowCreateSheet : MaintenanceEvent
    object HideCreateSheet : MaintenanceEvent
    data class OnNewTitleChange(val value: String) : MaintenanceEvent
    data class OnNewDescriptionChange(val value: String) : MaintenanceEvent
    data class OnNewDueMileageChange(val value: String) : MaintenanceEvent
    data class OnCompleteTask(val taskId: String) : MaintenanceEvent
    data class OnDeleteTask(val taskId: String) : MaintenanceEvent
    data class OnTaskClicked(val task: MaintenanceTask) : MaintenanceEvent
    object OnUserMessageShown : MaintenanceEvent
}
