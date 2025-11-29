package edu.ucne.loginapi.data.remote.dto

data class ChatMessageDto(
    val role: String,
    val content: String
)

data class ChatRequestDto(
    val conversationId: String,
    val messages: List<ChatMessageDto>
)

data class ChatResponseDto(
    val reply: String
)