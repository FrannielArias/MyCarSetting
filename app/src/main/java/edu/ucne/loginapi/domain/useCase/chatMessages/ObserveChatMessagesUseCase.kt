package edu.ucne.loginapi.domain.useCase.chatMessages

import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(conversationId: String): Flow<List<ChatMessage>> {
        return repository.observeMessages(conversationId)
    }
}
