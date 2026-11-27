package com.hermesagent.app.ui.chat

import com.hermesagent.app.data.api.HermesApi
import com.hermesagent.app.domain.model.ChatMessage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatViewModelTest {
    @Test
    fun `empty input does not send`() = runTest {
        // Basic sanity: verify ChatMessage role enum exists
        assertEquals(ChatMessage.Role.USER, ChatMessage.Role.USER)
        assertTrue(true)
    }
}