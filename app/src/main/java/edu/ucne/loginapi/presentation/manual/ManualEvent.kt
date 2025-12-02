package edu.ucne.loginapi.presentation.manual

sealed interface ManualEvent {
    object LoadInitialData : ManualEvent
    data class SelectTab(val index: Int) : ManualEvent
    data class OnWarningLightClicked(val id: Int) : ManualEvent
    data class OnGuideClicked(val id: Int) : ManualEvent
    object OnDismissDetail : ManualEvent
    object OnUserMessageShown : ManualEvent
}