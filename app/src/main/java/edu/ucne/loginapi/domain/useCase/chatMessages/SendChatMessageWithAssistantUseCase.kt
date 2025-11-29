package edu.ucne.loginapi.domain.useCase.chatMessages

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.ChatRepository
import javax.inject.Inject

class SendChatMessageWithAssistantUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(conversationId: String, text: String): Resource<Unit> {
        return repository.sendMessageWithAssistant(conversationId, text)
    }
}
