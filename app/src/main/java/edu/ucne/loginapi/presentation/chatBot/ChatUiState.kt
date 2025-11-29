package edu.ucne.loginapi.presentation.chatBot

import edu.ucne.loginapi.domain.model.ChatMessage

data class ChatUiState(
    val conversationId: String = "default",
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val userMessage: String? = null
)