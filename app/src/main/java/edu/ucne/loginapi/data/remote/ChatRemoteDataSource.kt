package edu.ucne.loginapi.data.remote

import edu.ucne.loginapi.data.mapper.toChatRequest
import edu.ucne.loginapi.data.mapper.toDomain
import edu.ucne.loginapi.data.remote.api.ChatApiService
import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.model.Resource
import javax.inject.Inject

class ChatRemoteDataSource @Inject constructor(
    private val api: ChatApiService
) {
    suspend fun getMessages(conversationId: String): Resource<List<ChatMessage>> {
        return try {
            val response = api.getMessages(conversationId)
            if (response.isSuccessful) {
                val body = response.body().orEmpty()
                Resource.Success(body.map { it.toDomain() })
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun sendMessage(
        message: ChatMessage,
        vehicleId: String?
    ): Resource<ChatMessage> {
        return try {
            val request = message.toChatRequest(vehicleId)
            val response = api.sendMessage(request)
            if (response.isSuccessful) {
                val body = response.body() ?: return Resource.Error("Empty response")
                Resource.Success(body.toDomain(message.conversationId))
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }
}
