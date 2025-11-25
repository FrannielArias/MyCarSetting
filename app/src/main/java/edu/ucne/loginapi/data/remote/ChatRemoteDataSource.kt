package edu.ucne.loginapi.data.remote

import edu.ucne.loginapi.data.remote.api.ChatApiService
import edu.ucne.loginapi.domain.model.ChatMessage
import javax.inject.Inject

class ChatRemoteDataSource @Inject constructor(
    private val api: ChatApiService
) {
    suspend fun getMessages(conversationId: String): Resource<List<ChatMessage>> {
        return Resource.Error("Remote chat not implemented yet")
    }

    suspend fun sendMessage(
        message: ChatMessage,
        vehicleId: String?
    ): Resource<ChatMessage> {
        return Resource.Error("Remote chat not implemented yet")
    }
}
