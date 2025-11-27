package edu.ucne.loginapi.presentation.manual

sealed interface ManualEvent {
    object LoadInitialData : ManualEvent
    data class SelectTab(val index: Int) : ManualEvent
    data class OnWarningLightClicked(val id: String) : ManualEvent
    data class OnGuideClicked(val id: String) : ManualEvent
    object OnDismissDetail : ManualEvent
    object OnUserMessageShown : ManualEvent
}
