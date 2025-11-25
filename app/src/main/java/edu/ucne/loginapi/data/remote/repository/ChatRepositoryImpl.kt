package edu.ucne.loginapi.data.repository

import edu.ucne.loginapi.data.ChatMessageDao
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
        chatMessageDao.observeMessages(conversationId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun sendMessageLocal(message: ChatMessage): Resource<Unit> {
        chatMessageDao.insert(message.toEntity())
        return Resource.Success(Unit)
    }

    override suspend fun clearConversation(conversationId: String): Resource<Unit> {
        chatMessageDao.clearConversation(conversationId)
        return Resource.Success(Unit)
    }

    override suspend fun syncConversation(
        conversationId: String,
        vehicleId: String?
    ): Resource<Unit> {
        // Por ahora no hacemos sync remoto real
        return Resource.Success(Unit)
    }

    override suspend fun syncFromRemote(conversationId: String): Resource<Unit> {
        // Por ahora no hacemos sync remoto real
        return Resource.Success(Unit)
    }

    override suspend fun postPendingMessages(): Resource<Unit> {
        // Pensado para WorkManager en el futuro.
        return Resource.Success(Unit)
    }
}
