package edu.ucne.loginapi.data.repository

import edu.ucne.loginapi.data.ChatMessageDao
import edu.ucne.loginapi.data.mapper.toDomain
import edu.ucne.loginapi.data.remote.ChatRemoteDataSource
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.toDomain
import edu.ucne.loginapi.data.toEntity
import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.repository.ChatRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val remote: ChatRemoteDataSource
) : ChatRepository {

    override fun observeMessages(conversationId: String): Flow<List<ChatMessage>> =
        chatMessageDao.observeMessages(conversationId).map { list -> list.map { it.toDomain() } }

    override suspend fun sendMessageLocal(message: ChatMessage): Resource<Unit> {
        chatMessageDao.insert(message.toEntity())
        return Resource.Success(Unit)
    }

    override suspend fun clearConversation(conversationId: String): Resource<Unit> {
        chatMessageDao.clearConversation(conversationId)
        return Resource.Success(Unit)
    }

    override suspend fun syncConversation(conversationId: String, vehicleId: String?): Resource<Unit> {
        val pending = chatMessageDao.getPendingMessages(conversationId)
        for (entity in pending) {
            val domain = entity.toDomain()
            when (val result = remote.sendMessage(domain, vehicleId)) {
                is Resource.Success -> {
                    val reply = result.data ?: continue
                    chatMessageDao.markAsSynced(domain.id)
                    chatMessageDao.insert(reply.toEntity())
                }
                is Resource.Error -> return Resource.Error("Error sync chat")
                else -> {}
            }
        }
        return Resource.Success(Unit)
    }

    override suspend fun syncFromRemote(conversationId: String): Resource<Unit> {
        val result = remote.getMessages(conversationId)
        return when (result) {
            is Resource.Success -> {
                val list = result.data.orEmpty()
                chatMessageDao.replaceConversation(
                    conversationId,
                    list.map { it.toEntity() }
                )
                Resource.Success(Unit)
            }
            is Resource.Error -> result
            is Resource.Loading -> Resource.Loading()
        }
    }
}
