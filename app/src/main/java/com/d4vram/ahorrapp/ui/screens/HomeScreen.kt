package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Search
import com.d4vram.ahorrapp.R
import com.d4vram.ahorrapp.viewmodel.BulkSyncResult

@Composable
fun HomeScreen(
    onScan: () -> Unit,
    onHistory: () -> Unit,
    onComparison: () -> Unit,
    onFavorites: () -> Unit,
    onSettings: () -> Unit,
    pendingSyncCount: Int,
    isSyncingPendingEntries: Boolean,
    onSyncAllPendingEntries: ((BulkSyncResult) -> Unit) -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    var showSyncAllDialog by remember { mutableStateOf(false) }
    var bulkSyncResult by remember { mutableStateOf<BulkSyncResult?>(null) }
    var syncInfoMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(androidx.compose.foundation.rememberScrollState()), // Enable scrolling for small screens
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_ahorrapp_foreground),
                contentDescription = "Logo AhorrApp",
                modifier = Modifier.size(56.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("AhorrApp", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Escanea, comparte y ahorra en Canarias", style = MaterialTheme.typography.bodyMedium)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Switch keeps its size
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onToggleTheme() },
                    thumbContent = {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.Nightlight else Icons.Filled.WbSunny,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    androidx.compose.material3.IconButton(
                        onClick = {
                            if (pendingSyncCount <= 0) {
                                syncInfoMessage = "No hay registros pendientes de sincronizar."
                            } else {
                                showSyncAllDialog = true
                            }
                        },
                        enabled = !isSyncingPendingEntries,
                        modifier = Modifier.size(24.dp)
                    ) {
                        if (isSyncingPendingEntries) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sincronizar pendientes con Supabase",
                                tint = if (pendingSyncCount > 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    androidx.compose.material3.IconButton(
                        onClick = onSettings,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = if (pendingSyncCount > 0) "$pendingSyncCount pendientes" else "Sync OK",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (pendingSyncCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Empezar a escanear", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Escanea el código de barras, añade el precio y ayúdanos a comparar supermercados.", style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onScan, modifier = Modifier.fillMaxWidth()) {
                    Text("Escanear producto")
                }
            }
        }

        Text("Tu actividad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) { // Weight ensures text fills space but respects button
                    Text("Historial de precios", fontWeight = FontWeight.SemiBold)
                    Text("Revisa tus aportaciones", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onHistory) { Text("Ver historial") }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) { // Weight for responsiveness
                    Text("Comparador de precios", fontWeight = FontWeight.SemiBold)
                    Text("Busca y compara ofertas", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onComparison) { 
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Buscar") 
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Productos favoritos", fontWeight = FontWeight.SemiBold)
                    Text("Alertas de precios", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onFavorites,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Favoritos")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp)) // Fixed space instead of weight(1f)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Desarrollado por D4vRAM, con amor por el open source desde Gran Canaria ❤️🇮🇨 \n\n " +
                        "Toda aportación a esta app es bienvenida, por pequeña que sea. Se aceptan también además de método de pago convencional en criptomonedas. Direcciones en la Bio de BuyMeACoffee.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { uriHandler.openUri("https://www.buymeacoffee.com/D4vRAM369") },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFFFFDD00)),
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        "☕ Buy me a Coffee",
                        color = androidx.compose.ui.graphics.Color.Black,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge
                    )
                }

                Button(
                    onClick = { uriHandler.openUri("https://github.com/D4vRAM369/") },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Black),
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_github),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        "GitHub Profile",
                        color = androidx.compose.ui.graphics.Color.White,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Nuevo botón para el repositorio de AhorrApp
            Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.material3.OutlinedButton(
                onClick = { uriHandler.openUri("https://github.com/D4vRAM369/AhorrApp/") },
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF238636), // Verde GitHub
                    contentColor = androidx.compose.ui.graphics.Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.7f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_github),
                        contentDescription = "GitHub",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Ver Repositorio",
                        style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }
            }

        }
    }

    if (showSyncAllDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showSyncAllDialog = false },
            title = { Text("Sincronizar pendientes") },
            text = {
                Text("Se sincronizarán $pendingSyncCount registros pendientes con Supabase y también se actualizará la tabla de productos para que aparezcan al escanear.")
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        showSyncAllDialog = false
                        onSyncAllPendingEntries { result ->
                            bulkSyncResult = result
                        }
                    }
                ) {
                    Text("Sí, sincronizar")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showSyncAllDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    bulkSyncResult?.let { result ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { bulkSyncResult = null },
            title = { Text("Resultado de sincronización") },
            text = {
                Text(
                    "Pendientes: ${result.totalPending}\n" +
                        "Sincronizados: ${result.syncedCount}\n" +
                        "Fallidos: ${result.failedCount}"
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { bulkSyncResult = null }) {
                    Text("OK")
                }
            }
        )
    }

    if (syncInfoMessage != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { syncInfoMessage = null },
            text = { Text(syncInfoMessage!!) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { syncInfoMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}
