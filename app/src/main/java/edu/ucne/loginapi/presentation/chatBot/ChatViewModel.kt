package edu.ucne.loginapi.presentation.chatBot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.useCase.chatMessages.ClearConversationUseCase
import edu.ucne.loginapi.domain.useCase.chatMessages.ObserveChatMessagesUseCase
import edu.ucne.loginapi.domain.useCase.chatMessages.SendChatMessageWithAssistantUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    private val sendChatMessageWithAssistantUseCase: SendChatMessageWithAssistantUseCase,
    private val clearConversationUseCase: ClearConversationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.Initialize -> initialize(event.conversationId)
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

    private fun initialize(conversationId: String) {
        val currentId = _state.value.conversationId
        if (currentId == conversationId && currentId.isNotBlank()) return
        _state.update { it.copy(conversationId = conversationId) }
        loadMessages(conversationId)
    }

    private fun loadMessages(conversationId: String) {
        if (conversationId.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            observeChatMessagesUseCase(conversationId).collectLatest { list ->
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
        val conversationId = _state.value.conversationId
        if (text.isBlank() || conversationId.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(inputText = "", isSending = true) }
            when (val result = sendChatMessageWithAssistantUseCase(conversationId, text)) {
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isSending = false,
                            userMessage = result.message ?: "Error al enviar mensaje"
                        )
                    }
                }
                is Resource.Success -> {
                    _state.update { it.copy(isSending = false) }
                }
                else -> {
                    _state.update { it.copy(isSending = false) }
                }
            }
        }
    }

    private fun clearConversation() {
        val conversationId = _state.value.conversationId
        if (conversationId.isBlank()) return

        viewModelScope.launch {
            when (val result = clearConversationUseCase(conversationId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            messages = emptyList(),
                            userMessage = "Conversación limpiada"
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(userMessage = result.message ?: "Error al limpiar conversación")
                    }
                }
                else -> Unit
            }
        }
    }
}
