package com.hermesagent.app.ui.chat

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hermesagent.app.ui.theme.HermesAgentTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsPrompt() {
        composeTestRule.setContent {
            HermesAgentTheme {
                ChatScreen()
            }
        }
        composeTestRule.onNodeWithText("Start a conversation with Hermes").assertIsDisplayed()
    }
}