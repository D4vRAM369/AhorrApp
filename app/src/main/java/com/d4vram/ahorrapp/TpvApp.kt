package com.d4vram.ahorrapp

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.d4vram.ahorrapp.navigation.NavGraph

import com.d4vram.ahorrapp.viewmodel.TpvViewModel

@Composable
fun TpvApp(viewModel: TpvViewModel) {
    val navController = rememberNavController()

    Scaffold { padding ->
        NavGraph(navController, padding, viewModel)
    }
}
