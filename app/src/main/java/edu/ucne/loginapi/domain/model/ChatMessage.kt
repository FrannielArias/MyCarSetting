package edu.ucne.loginapi.domain.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val role: ChatRole,
    val content: String,
    val timestampMillis: Long,
    val isPendingCreate: Boolean = false
)

enum class ChatRole {
    USER,
    ASSISTANT
}
