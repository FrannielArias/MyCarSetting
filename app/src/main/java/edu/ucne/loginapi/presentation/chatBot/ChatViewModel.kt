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

        viewModelScope.launch {
            _state.update { it.copy(inputText = "") }
            val result = sendChatMessageWithAssistantUseCase(
                conversationId = _state.value.conversationId,
                text = text
            )
            if (result is Resource.Error) {
                _state.update {
                    it.copy(userMessage = result.message ?: "Error al enviar mensaje")
                }
            }
        }
    }

    private fun clearConversation() {
        viewModelScope.launch {
            val conversationId = _state.value.conversationId
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
