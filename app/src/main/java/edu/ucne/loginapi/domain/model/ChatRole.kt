package edu.ucne.loginapi.domain.model

enum class ChatRole {
    USER,
    ASSISTANT,
}
data class ChatTurn(
    val role: ChatRole,
    val content: String
)