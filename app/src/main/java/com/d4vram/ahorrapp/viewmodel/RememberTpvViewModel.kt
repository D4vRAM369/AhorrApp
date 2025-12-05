package com.d4vram.ahorrapp.viewmodel

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun rememberTpvViewModel(): TpvViewModel {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    return viewModel(factory = TpvViewModel.provideFactory(app))
}
