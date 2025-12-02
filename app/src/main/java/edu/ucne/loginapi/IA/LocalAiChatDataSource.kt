package edu.ucne.loginapi.data.chat

import edu.ucne.loginapi.IA.AiChatClient

class LocalAiChatDataSource(
    private val aiChatClient: AiChatClient
) {

    suspend fun getAssistantReply(userInput: String): String {
        val systemPrompt = """
            Eres un asistente mecánico dentro de una app llamada MyCarSetting.
            Respondes siempre en español, de forma clara, precisa y amigable.
            Ayudas al usuario a entender fallos, ruidos, luces del tablero
            y mantenimiento de su vehículo. Evita respuestas muy largas.
            Si la pregunta no tiene que ver con vehículos o mantenimiento, redirígela de forma educada.
        """.trimIndent()

        val finalPrompt = buildString {
            append(systemPrompt)
            append("\n\nUsuario: ")
            append(userInput)
            append("\nAsistente:")
        }

        return aiChatClient.generateReply(finalPrompt)
    }
}