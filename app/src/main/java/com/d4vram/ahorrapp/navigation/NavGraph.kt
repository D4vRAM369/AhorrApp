package com.d4vram.ahorrapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.d4vram.ahorrapp.ui.screens.HistoryScreen
import com.d4vram.ahorrapp.ui.screens.HomeScreen
import com.d4vram.ahorrapp.ui.screens.OnboardingScreen
import com.d4vram.ahorrapp.ui.screens.PriceEntryScreen
import com.d4vram.ahorrapp.ui.screens.ScannerScreen
import com.d4vram.ahorrapp.ui.screens.WelcomeScreen
import com.d4vram.ahorrapp.ui.screens.LockScreen
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.d4vram.ahorrapp.viewmodel.TpvViewModel

@Composable
fun NavGraph(nav: NavHostController, padding: PaddingValues, viewModel: TpvViewModel) {

    // --- SOLUTION ---
    // Move the LaunchedEffect here, into the Composable scope of NavGraph
    val isLocked by viewModel.isAppLocked.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()

    LaunchedEffect(isLocked) {
        if (isLocked) {
            nav.navigate("lock") {
                popUpTo(0) // Clear backstack so user can't go back
            }
        }
    }

    NavHost(
        navController = nav,
        startDestination = if (showOnboarding) "onboarding" else "welcome"
    ) {

        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    viewModel.completeOnboarding()
                    nav.navigate("welcome") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                onSkip = {
                    viewModel.completeOnboarding()
                    nav.navigate("welcome") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("welcome") {
            WelcomeScreen(onContinue = { nav.navigate("home") })
        }

        composable("lock") {
            LockScreen(deviceId = viewModel.getDeviceIdStr())
        }

        // The LaunchedEffect that was causing the error has been moved out.

        composable("home") {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            HomeScreen(
                onScan = { nav.navigate("scanner") },
                onHistory = { nav.navigate("history") },
                onComparison = { nav.navigate("comparison") },
                isDarkMode = isDarkMode,
                onToggleTheme = { viewModel.toggleDarkMode() }
            )
        }

        composable("scanner") {
            ScannerScreen { barcode ->
                nav.navigate("entry/$barcode")
            }
        }

        composable("entry/{barcode}") { backStack ->
            val code = backStack.arguments?.getString("barcode") ?: ""
            PriceEntryScreen(barcode = code, onDone = { nav.popBackStack() })
        }

        composable("history") {
            HistoryScreen(onSettingsClick = { nav.navigate("profile") })
        }

        composable("comparison") {
            com.d4vram.ahorrapp.ui.screens.ComparisonScreen(
                viewModel = viewModel,
                onBack = { nav.popBackStack() }
            )
        }

        composable("profile") {
            com.d4vram.ahorrapp.ui.screens.ProfileScreen(onBack = { nav.popBackStack() })
        }
    }
}
