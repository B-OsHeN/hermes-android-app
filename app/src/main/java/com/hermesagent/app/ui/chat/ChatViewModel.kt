package com.hermesagent.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermesagent.app.data.api.ChatRequest
import com.hermesagent.app.data.api.HermesApi
import com.hermesagent.app.data.db.ChatDao
import com.hermesagent.app.data.db.ChatMessageEntity
import com.hermesagent.app.domain.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

sealed interface ChatUiState {
    data object Idle : ChatUiState
    data object Loading : ChatUiState
    data class Error(val message: String) : ChatUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val api: HermesApi,
    private val chatDao: ChatDao
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            chatDao.getAll().collect { entities ->
                _messages.value = entities.map { e ->
                    ChatMessage(e.id, ChatMessage.Role.valueOf(e.role), e.content, e.timestamp)
                }
            }
        }
    }

    fun send(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(UUID.randomUUID().toString(), ChatMessage.Role.USER, text)
        _messages.value = _messages.value + userMsg
        viewModelScope.launch {
            chatDao.insert(ChatMessageEntity(userMsg.id, userMsg.role.name, userMsg.content, userMsg.timestamp))
        }
        _uiState.value = ChatUiState.Loading

        viewModelScope.launch {
            try {
                val response: Response<ResponseBody> = api.sendMessage(ChatRequest(text))
                if (response.isSuccessful && response.body() != null) {
                    val assistantId = UUID.randomUUID().toString()
                    var accumulated = ""
                    response.body()!!.source().use { source ->
                        while (!source.exhausted()) {
                            val line = source.readUtf8Line() ?: break
                            if (line.startsWith("data:")) {
                                val json = line.removePrefix("data:").trim()
                                if (json.isNotEmpty()) {
                                    val content = extractContent(json)
                                    accumulated += content
                                    updateAssistant(assistantId, accumulated)
                                }
                            }
                        }
                    }
                    viewModelScope.launch {
                        chatDao.insert(ChatMessageEntity(assistantId, ChatMessage.Role.ASSISTANT.name, accumulated, System.currentTimeMillis()))
                    }
                    _uiState.value = ChatUiState.Idle
                } else {
                    _uiState.value = ChatUiState.Error("Request failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun updateAssistant(id: String, content: String) {
        val list = _messages.value.toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) list[idx] = list[idx].copy(content = content)
        else list.add(ChatMessage(id, ChatMessage.Role.ASSISTANT, content))
        _messages.value = list
    }

    private fun extractContent(json: String): String {
        val regex = "\"content\"\\s*:\\s*\"([^\"]*)\"".toRegex()
        return regex.find(json)?.groupValues?.get(1) ?: ""
    }
}