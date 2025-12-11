// PushMessageScreen.kt - Pantalla para mostrar mensajes push
package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d4vram.ahorrapp.data.PushMessage
import com.d4vram.ahorrapp.viewmodel.TpvViewModel
import com.d4vram.ahorrapp.viewmodel.rememberTpvViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushMessageScreen(
    message: PushMessage,
    onDismiss: () -> Unit,
    onLinkClick: (String) -> Unit = {},
    viewModel: TpvViewModel = rememberTpvViewModel()
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(message.title) },
                navigationIcon = {
                    IconButton(onClick = {
                        // Marcar como visto (dismissed)
                        viewModel.markMessageViewed(message.id, "dismissed")
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono según tipo de mensaje
            val icon = when (message.messageType) {
                "info" -> "ℹ️"
                "warning" -> "⚠️"
                "success" -> "✅"
                "update" -> "🔄"
                else -> "📢"
            }

            Text(
                text = icon,
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje
            Text(
                text = message.message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Chip de enlace (si existe)
            message.linkUrl?.let { url ->
                AssistChip(
                    onClick = {
                        // Marcar como link clicked
                        viewModel.markMessageViewed(message.id, "link_clicked")
                        try {
                            uriHandler.openUri(url)
                        } catch (e: Exception) {
                            // Fallback al navegador del sistema
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse(url)
                            )
                            context.startActivity(intent)
                        }
                        onDismiss()
                    },
                    label = { Text(message.linkText ?: "Ver más") },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de continuar
            Button(
                onClick = {
                    // Marcar como visto
                    viewModel.markMessageViewed(message.id, "viewed")
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Entendido")
            }
        }
    }
}

// Data class para PushMessage
data class PushMessage(
    val id: String,
    val title: String,
    val message: String,
    val messageType: String = "info",
    val linkUrl: String? = null,
    val linkText: String? = null,
    val priority: Int = 0
)