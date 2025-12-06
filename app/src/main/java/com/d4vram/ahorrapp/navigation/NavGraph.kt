package com.d4vram.ahorrapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.d4vram.ahorrapp.ui.screens.HistoryScreen
import com.d4vram.ahorrapp.ui.screens.HomeScreen
import com.d4vram.ahorrapp.ui.screens.PriceEntryScreen
import com.d4vram.ahorrapp.ui.screens.ScannerScreen
import com.d4vram.ahorrapp.ui.screens.WelcomeScreen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.d4vram.ahorrapp.viewmodel.TpvViewModel

@Composable
fun NavGraph(nav: NavHostController, padding: PaddingValues, viewModel: TpvViewModel) {

    NavHost(
        navController = nav,
        startDestination = "welcome"
    ) {

        composable("welcome") {
            WelcomeScreen(onContinue = { nav.navigate("home") })
        }

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
