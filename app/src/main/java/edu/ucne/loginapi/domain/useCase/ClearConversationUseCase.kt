package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.ChatRepository

class ClearConversationUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(conversationId: String): Resource<Unit> =
        repository.clearConversation(conversationId)
}
