package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(conversationId: String): Flow<List<ChatMessage>>
    suspend fun sendMessageWithAssistant(conversationId: String, text: String): Resource<Unit>
    suspend fun clearConversation(conversationId: String): Resource<Unit>
}