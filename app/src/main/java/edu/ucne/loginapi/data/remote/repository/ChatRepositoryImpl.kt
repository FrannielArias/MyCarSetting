package edu.ucne.loginapi.data.remote.repository

import edu.ucne.loginapi.data.chat.LocalAiChatDataSource
import edu.ucne.loginapi.data.dao.ChatMessageDao
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toEntity
import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.model.ChatRole
import edu.ucne.loginapi.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val localAiChatDataSource: LocalAiChatDataSource
) : ChatRepository {

    override fun observeMessages(conversationId: String): Flow<List<ChatMessage>> {
        return chatMessageDao.observeMessages(conversationId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun sendMessageWithAssistant(
        conversationId: String,
        text: String
    ): Resource<Unit> {
        return try {
            val now = System.currentTimeMillis()

            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = ChatRole.USER,
                content = text,
                timestampMillis = now,
                isPendingSync = false
            )
            chatMessageDao.insertMessage(userMessage.toEntity())

            val assistantReply = localAiChatDataSource.getAssistantReply(text)

            val assistantMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = ChatRole.ASSISTANT,
                content = assistantReply,
                timestampMillis = System.currentTimeMillis(),
                isPendingSync = false
            )
            chatMessageDao.insertMessage(assistantMessage.toEntity())

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al generar la respuesta del asistente")
        }
    }

    override suspend fun clearConversation(conversationId: String): Resource<Unit> {
        return try {
            chatMessageDao.clearConversation(conversationId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al limpiar conversación")
        }
    }
}