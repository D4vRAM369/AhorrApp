package com.d4vram.ahorrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.d4vram.ahorrapp.ui.theme.AhorrappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: com.d4vram.ahorrapp.viewmodel.TpvViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            AhorrappTheme(darkTheme = isDarkMode, dynamicColor = false) {
                TpvApp(viewModel)
            }
        }
    }
}
