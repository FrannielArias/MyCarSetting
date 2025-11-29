package edu.ucne.loginapi.data.remote.repository

import edu.ucne.loginapi.data.dao.ChatMessageDao
import edu.ucne.loginapi.data.remote.ChatApi
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.mappers.toChatRequestDto
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toEntity
import edu.ucne.loginapi.data.remote.mappers.toResponseDomain
import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.model.ChatRole
import edu.ucne.loginapi.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val chatApi: ChatApi
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
            val timestamp = System.currentTimeMillis()

            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = ChatRole.USER,
                content = text,
                timestampMillis = timestamp,
                isPendingSync = true
            )
            chatMessageDao.insertMessage(userMessage.toEntity())

            val messages = chatMessageDao.observeMessages(conversationId)
                .first()
                .map { it.toDomain() }

            val request = messages.toChatRequestDto()
            val response = chatApi.sendMessage(request)

            val assistantMessage = response.toResponseDomain(conversationId)
            chatMessageDao.insertMessage(assistantMessage.toEntity())

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al comunicarse con el asistente")
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