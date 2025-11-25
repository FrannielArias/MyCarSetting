package edu.ucne.loginapi.data.remote.dto

data class ChatMessageDto(
    val id: String,
    val conversationId: String,
    val role: String,
    val content: String,
    val timestampMillis: Long
)

data class ChatRequestDto(
    val conversationId: String,
    val userMessage: String,
    val vehicleId: String?
)

data class ChatResponseDto(
    val messageId: String,
    val conversationId: String,
    val reply: String,
    val timestampMillis: Long
)
