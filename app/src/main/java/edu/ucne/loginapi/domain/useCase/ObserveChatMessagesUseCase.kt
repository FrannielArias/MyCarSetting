package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ObserveChatMessagesUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(conversationId: String): Flow<List<ChatMessage>> =
        repository.observeMessages(conversationId)
}
