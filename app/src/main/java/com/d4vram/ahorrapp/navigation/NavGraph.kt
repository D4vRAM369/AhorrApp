package com.d4vram.ahorrapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.d4vram.ahorrapp.viewmodel.TpvViewModel
import com.d4vram.ahorrapp.viewmodel.rememberTpvViewModel
import com.d4vram.ahorrapp.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController, padding: androidx.compose.foundation.layout.PaddingValues, viewModel: TpvViewModel) {
    val showOnboarding by viewModel.showOnboarding.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (showOnboarding) "onboarding" else "welcome",
        modifier = Modifier.padding(padding)
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    viewModel.completeOnboarding()
                    navController.navigate("welcome") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                onSkip = {
                    viewModel.completeOnboarding()
                    navController.navigate("welcome") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("welcome") {
            WelcomeScreen(onContinue = { navController.navigate("home") })
        }

        composable("home") {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentPushMessage by viewModel.currentPushMessage.collectAsState()
            val pendingSyncCount by viewModel.observePendingSyncCount().collectAsState(initial = 0)

            if (currentPushMessage != null) {
                PushMessageScreen(
                    message = currentPushMessage!!,
                    onDismiss = { viewModel.dismissPushMessage() },
                    viewModel = viewModel
                )
            } else {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    viewModel.loadPushMessagesForWelcome()
                }
                HomeScreen(
                    onScan = { navController.navigate("scanner") },
                    onHistory = { navController.navigate("history") },
                    onComparison = { navController.navigate("comparison") },
                    onFavorites = { navController.navigate("favorites") },
                    onSettings = { navController.navigate("profile") },
                    pendingSyncCount = pendingSyncCount,
                    isSyncingPendingEntries = viewModel.isSyncingPendingEntries,
                    onSyncAllPendingEntries = { onResult -> viewModel.syncAllPendingEntries(onResult) },
                    isDarkMode = isDarkMode,
                    onToggleTheme = { viewModel.toggleDarkMode() }
                )
            }
        }

        composable("scanner") {
            ScannerScreen(viewModel = viewModel) { barcode ->
                navController.navigate("entry/$barcode")
            }
        }

        composable("entry/{barcode}") { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            PriceEntryScreen(
                barcode = barcode,
                onDone = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable("history") {
            HistoryScreen(
                viewModel = viewModel,
                onSettingsClick = { navController.navigate("profile") }
            )
        }

        composable("comparison") {
            ComparisonScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
