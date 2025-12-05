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

@Composable
fun NavGraph(nav: NavHostController, padding: PaddingValues) {

    NavHost(
        navController = nav,
        startDestination = "welcome"
    ) {

        composable("welcome") {
            WelcomeScreen(onContinue = { nav.navigate("home") })
        }

        composable("home") {
            HomeScreen(
                onScan = { nav.navigate("scanner") },
                onHistory = { nav.navigate("history") },
                onMyProducts = { nav.navigate("history") }
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

        composable("profile") {
            com.d4vram.ahorrapp.ui.screens.ProfileScreen(onBack = { nav.popBackStack() })
        }
    }
}
