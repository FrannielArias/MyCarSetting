package edu.ucne.loginapi.domain.useCase.chatMessages

import edu.ucne.loginapi.domain.repository.ChatRepository
import javax.inject.Inject

class ObserveChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(conversationId: String) = repository.observeMessages(conversationId)
}
