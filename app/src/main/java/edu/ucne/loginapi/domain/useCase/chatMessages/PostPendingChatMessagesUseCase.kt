package edu.ucne.loginapi.domain.useCase.chatMessages

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.ChatRepository
import jakarta.inject.Inject

class PostPendingChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(): Resource<Unit> =
        repository.postPendingMessages()
}
