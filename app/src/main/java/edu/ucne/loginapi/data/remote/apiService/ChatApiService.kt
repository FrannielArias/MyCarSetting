package edu.ucne.loginapi.data.remote.apiService

import edu.ucne.loginapi.data.remote.dto.ChatMessageDto
import edu.ucne.loginapi.data.remote.dto.ChatRequestDto
import edu.ucne.loginapi.data.remote.dto.ChatResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApiService {
    @GET("api/chat/{conversationId}/messages")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String
    ): Response<List<ChatMessageDto>>

    @POST("api/chat/send")
    suspend fun sendMessage(
        @Body request: ChatRequestDto
    ): Response<ChatResponseDto>
}