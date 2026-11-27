package com.hermesagent.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hermesagent.app.ui.chat.ChatScreen
import com.hermesagent.app.ui.onboarding.OnboardingScreen
import com.hermesagent.app.ui.settings.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(onGetStarted = { navController.navigate("chat") })
        }
        composable("chat") { ChatScreen() }
        composable("settings") { SettingsScreen() }
    }
}