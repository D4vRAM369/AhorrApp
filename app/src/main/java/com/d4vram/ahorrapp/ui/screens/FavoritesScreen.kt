package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.d4vram.ahorrapp.data.UserFavorite
import com.d4vram.ahorrapp.data.PriceAlert
import com.d4vram.ahorrapp.viewmodel.TpvViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: TpvViewModel,
    onBack: () -> Unit
) {
    val userFavorites by viewModel.userFavorites.collectAsState()
    val userAlerts by viewModel.userAlerts.collectAsState()
    val isLoading = viewModel.isLoadingFavorites

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos Favoritos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Tus productos favoritos con alertas de precio",
                style = MaterialTheme.typography.titleMedium
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (userFavorites.isEmpty()) {
                // Estado vacío
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Text(
                            "No tienes productos favoritos aún",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Text(
                            "Añade productos favoritos desde la pantalla de comparación para recibir alertas de precios",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(userFavorites) { favorite ->
                        FavoriteItemCard(
                            favorite = favorite,
                            alert = userAlerts.find { it.barcode == favorite.barcode },
                            onToggleAlert = { enabled ->
                                if (enabled) {
                                    viewModel.createPriceAlert(favorite.barcode)
                                } else {
                                    viewModel.removePriceAlert(favorite.barcode)
                                }
                            },
                            onRemoveFavorite = {
                                viewModel.removeFromFavorites(favorite.barcode)
                            },
                            onConfigureAlert = { percentage, targetPrice ->
                                viewModel.createPriceAlert(favorite.barcode, targetPrice, percentage)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteItemCard(
    favorite: UserFavorite,
    alert: PriceAlert?,
    onToggleAlert: (Boolean) -> Unit,
    onRemoveFavorite: () -> Unit,
    onConfigureAlert: (Double, Double?) -> Unit = { _, _ -> }
) {
    var showAlertDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = favorite.productName ?: "Producto sin nombre",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                Text(
                    text = "Código: ${favorite.barcode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                if (alert != null) {
                    Text(
                        text = "Alerta: ${alert.alertPercentage}% de bajada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de alerta
                IconButton(
                    onClick = {
                        if (alert == null) {
                            // Activar alerta con configuración por defecto
                            onToggleAlert(true)
                        } else {
                            // Mostrar diálogo para configurar
                            showAlertDialog = true
                        }
                    }
                ) {
                    Icon(
                        if (alert != null) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                        contentDescription = if (alert != null) "Configurar alerta" else "Activar alerta",
                        tint = if (alert != null) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                // Botón quitar favorito
                IconButton(onClick = onRemoveFavorite) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Quitar favorito",
                        tint = Color.Red
                    )
                }
            }
        }
    }

    // Diálogo de configuración de alerta
    if (showAlertDialog && alert != null) {
        var alertPercentage by remember { mutableStateOf(alert.alertPercentage) }
        var targetPrice by remember { mutableStateOf(alert.targetPrice?.toString() ?: "") }

        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text("Configurar Alerta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("¿Qué tipo de alerta quieres?")

                    OutlinedTextField(
                        value = alertPercentage.toString(),
                        onValueChange = {
                            alertPercentage = it.toDoubleOrNull() ?: 10.0
                        },
                        label = { Text("Porcentaje de bajada (%)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )

                    OutlinedTextField(
                        value = targetPrice,
                        onValueChange = { targetPrice = it },
                        label = { Text("Precio objetivo (€) - Opcional") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val target = targetPrice.toDoubleOrNull()
                        onConfigureAlert(alertPercentage, target)
                        showAlertDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onToggleAlert(false) // Desactivar alerta
                        showAlertDialog = false
                    }
                ) {
                    Text("Desactivar")
                }
            }
        )
    }
}