package edu.ucne.loginapi.presentation.chatBot

 sealed interface ChatEvent {
    data object LoadInitialData : ChatEvent
    data class OnInputChange(val value: String) : ChatEvent
    data object OnSendMessage : ChatEvent
    data object OnClearConversation : ChatEvent
    data object OnUserMessageShown : ChatEvent
}