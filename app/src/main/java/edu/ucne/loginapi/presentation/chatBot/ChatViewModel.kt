package edu.ucne.loginapi.presentation.chatBot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.model.ChatRole
import edu.ucne.loginapi.domain.useCase.chatMessages.ClearConversationUseCase
import edu.ucne.loginapi.domain.useCase.chatMessages.ObserveChatMessagesUseCase
import edu.ucne.loginapi.domain.useCase.chatMessages.SendChatMessageLocalUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    private val sendChatMessageLocalUseCase: SendChatMessageLocalUseCase,
    private val clearConversationUseCase: ClearConversationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    init {
        onEvent(ChatEvent.LoadInitialData)
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            ChatEvent.LoadInitialData -> loadMessages()
            is ChatEvent.OnInputChange -> {
                _state.update { it.copy(inputText = event.value) }
            }
            ChatEvent.OnSendMessage -> sendMessage()
            ChatEvent.OnClearConversation -> clearConversation()
            ChatEvent.OnUserMessageShown -> {
                _state.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            observeChatMessagesUseCase(_state.value.conversationId).collectLatest { list ->
                _state.update {
                    it.copy(
                        messages = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isBlank()) return

        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = _state.value.conversationId,
            role = ChatRole.USER,
            content = text,
            timestampMillis = System.currentTimeMillis(),
            isPendingCreate = true
        )

        viewModelScope.launch {
            _state.update { it.copy(inputText = "") }
            when (val result = sendChatMessageLocalUseCase(message)) {
                is Resource.Success -> {}
                is Resource.Error -> {
                    _state.update {
                        it.copy(userMessage = result.message ?: "Error al enviar mensaje")
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun clearConversation() {
        viewModelScope.launch {
            when (val result = clearConversationUseCase(_state.value.conversationId)) {
                is Resource.Success -> {
                    _state.update { it.copy(userMessage = "Conversación limpiada") }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(userMessage = result.message ?: "Error al limpiar conversación")
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
}