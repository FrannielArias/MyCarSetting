package edu.ucne.loginapi.domain.useCase.chatMessages

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.repository.ChatRepository
import javax.inject.Inject

class SendChatMessageLocalUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(message: ChatMessage): Resource<Unit> {
        return repository.sendMessageLocal(message)
    }
}
