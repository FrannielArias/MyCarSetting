package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.repository.ChatRepository

class SendChatMessageLocalUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(message: ChatMessage): Resource<ChatMessage> =
        repository.sendMessageLocal(message)
}
