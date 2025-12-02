package edu.ucne.loginapi.IA

class HybridAiChatClient(
    private val nanoClient: AiChatClient,
    private val fallbackClient: AiChatClient
) : AiChatClient {

    override suspend fun generateReply(
        userMessage: String,
        history: List<Pair<String, String>>
    ): String {
        val nanoResult = nanoClient.generateReply(userMessage, history)

        return if (
            nanoResult == "GEMINI_NANO_UNAVAILABLE" ||
            nanoResult == "GEMINI_NANO_EMPTY_RESPONSE"
        ) {
            fallbackClient.generateReply(userMessage, history)
        } else {
            nanoResult
        }
    }
}