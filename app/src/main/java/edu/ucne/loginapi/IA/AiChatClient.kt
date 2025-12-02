package edu.ucne.loginapi.IA

interface AiChatClient {
    suspend fun generateReply(
        userMessage: String,
        history: List<Pair<String, String>> = emptyList()
    ): String
}
