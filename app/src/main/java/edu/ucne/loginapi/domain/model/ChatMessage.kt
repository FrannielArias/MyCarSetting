package edu.ucne.loginapi.domain.model

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val role: ChatRole,
    val content: String,
    val timestampMillis: Long,
    val isPendingSync: Boolean
)