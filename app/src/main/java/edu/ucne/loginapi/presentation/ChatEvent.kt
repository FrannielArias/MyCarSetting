package edu.ucne.loginapi.presentation

sealed interface ChatEvent {
    object LoadInitialData : ChatEvent
    data class OnInputChange(val value: String) : ChatEvent
    object OnSendMessage : ChatEvent
    object OnClearConversation : ChatEvent
    object OnUserMessageShown : ChatEvent
}
