package com.d4vram.ahorrapp

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.d4vram.ahorrapp.navigation.NavGraph

@Composable
fun TpvApp() {
    val navController = rememberNavController()

    Scaffold { padding ->
        NavGraph(navController, padding)
    }
}
