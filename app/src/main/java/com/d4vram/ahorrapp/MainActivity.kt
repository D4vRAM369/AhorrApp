package com.d4vram.ahorrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.d4vram.ahorrapp.ui.theme.AhorrappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AhorrappTheme {
                TpvApp()
            }
        }
    }
}
