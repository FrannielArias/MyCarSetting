package edu.ucne.loginapi.domain.useCase.chatMessages

import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.repository.ChatRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(conversationId: String): Flow<List<ChatMessage>> =
        repository.observeMessages(conversationId)
}
