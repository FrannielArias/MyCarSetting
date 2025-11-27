package edu.ucne.loginapi.presentation.chatBot

import edu.ucne.loginapi.domain.model.ChatMessage

data class ChatUiState(
    val isLoading: Boolean = true,
    val conversationId: String = "default",
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val userMessage: String? = null
)
