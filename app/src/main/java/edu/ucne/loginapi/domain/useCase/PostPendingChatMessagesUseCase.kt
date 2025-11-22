package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.ChatRepository

class PostPendingChatMessagesUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(): Resource<Unit> =
        repository.postPendingMessages()
}
