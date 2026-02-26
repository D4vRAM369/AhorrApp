package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d4vram.ahorrapp.data.PriceEntryEntity
import com.d4vram.ahorrapp.viewmodel.TpvViewModel
import com.d4vram.ahorrapp.viewmodel.rememberTpvViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: TpvViewModel = rememberTpvViewModel(),
    onSettingsClick: () -> Unit
) {
    val history by viewModel.observeHistory().collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf<PriceEntryEntity?>(null) }
    var showEditDialog by remember { mutableStateOf<PriceEntryEntity?>(null) }
    var showSyncDialog by remember { mutableStateOf<PriceEntryEntity?>(null) }
    var showAddPriceDialog by remember { mutableStateOf<PriceEntryEntity?>(null) }
    var syncResultInfo by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Historial de precios",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            AssistChip(
                onClick = onSettingsClick,
                label = { Text("Perfil") },
                trailingIcon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes", modifier = Modifier.size(18.dp)) },
                border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            )
        }

        Text("Últimos productos aportados", style = MaterialTheme.typography.bodyMedium)

        if (history.isEmpty()) {
            Spacer(Modifier.height(20.dp))
            Text(
                "Aún no hay precios guardados. Escanea tu primer producto.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(history) { entry ->
                    HistoryCard(
                        entry = entry,
                        onDelete = { showDeleteDialog = entry },
                        onEdit = { showEditDialog = entry },
                        onSync = { showSyncDialog = entry },
                        onAddPrice = { showAddPriceDialog = entry }
                    )
                }
            }
        }
    }

    // --- Dialogs ---

    // Add Price Dialog
    if (showAddPriceDialog != null) {
        val entry = showAddPriceDialog!!
        var newSupermarket by remember { mutableStateOf("") }
        var newPrice by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        val supermarkets = listOf("Mercadona", "Hiperdino", "SPAR", "Lidl", "Carrefour", "Alcampo", "Aldi")

        AlertDialog(
            onDismissRequest = { showAddPriceDialog = null },
            title = { Text("Añadir otro precio") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Producto: ${entry.productName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    androidx.compose.foundation.layout.Box {
                        OutlinedTextField(
                            value = newSupermarket,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Supermercado") },
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true }
                        )
                        androidx.compose.material3.DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            supermarkets.forEach { s ->
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = { newSupermarket = s; expanded = false }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newPrice,
                        onValueChange = { newPrice = it },
                        label = { Text("Precio (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val p = newPrice.toDoubleOrNull()
                    if (p != null && newSupermarket.isNotBlank()) {
                        viewModel.addExtraPrice(entry, newSupermarket, p)
                        showAddPriceDialog = null
                    }
                }) {
                    Text("Añadir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPriceDialog = null }) { Text("Cancelar") }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog != null) {
        val entry = showDeleteDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Borrar registro") },
            text = { Text("¿Estás seguro de borrar '${entry.productName}'? Esta acción solo afectará a tu historial local.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEntry(entry.id)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancelar") }
            }
        )
    }

    // Edit Dialog
    if (showEditDialog != null) {
        val entry = showEditDialog!!
        var editName by remember { mutableStateOf(entry.productName ?: "") }
        var editPrice by remember { mutableStateOf(entry.price.toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("Editar producto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nombre") }
                    )
                    OutlinedTextField(
                        value = editPrice,
                        onValueChange = { editPrice = it },
                        label = { Text("Precio (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newPrice = editPrice.toDoubleOrNull() ?: entry.price
                    val updatedEntry = entry.copy(
                        productName = editName,
                        price = newPrice
                    )
                    viewModel.updateEntry(updatedEntry)
                    showEditDialog = null
                }) {
                    Text("Guardar cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) { Text("Cancelar") }
            }
        )
    }

    // Sync Dialog
    if (showSyncDialog != null) {
        val entry = showSyncDialog!!
        AlertDialog(
            onDismissRequest = { showSyncDialog = null },
            title = { Text("Sincronizar con la nube") },
            text = {
                Text(
                    "Se sincronizarán los datos actualizados a la nube. Por favor, asegúrate de que sean verídicos.\n\nGracias por tu colaboración: entre todos podemos."
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.syncEntry(entry) { success ->
                        syncResultInfo = if (success) "Sincronizado correctamente" else "Error al sincronizar"
                    }
                    showSyncDialog = null
                }) {
                    Text("Sí, sincronizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSyncDialog = null }) { Text("Cancelar") }
            }
        )
    }

    // Sync result feedback
    if (syncResultInfo != null) {
        AlertDialog(
            onDismissRequest = { syncResultInfo = null },
            text = { Text(syncResultInfo!!) },
            confirmButton = {
                TextButton(onClick = { syncResultInfo = null }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun rememberFormatted(timestamp: Long): String {
    return remember(timestamp) {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        formatter.format(Date(timestamp))
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HistoryCard(
    entry: PriceEntryEntity,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onSync: () -> Unit,
    onAddPrice: () -> Unit
) {
    val date = rememberFormatted(entry.timestamp)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(entry.productName ?: "Producto sin nombre", fontWeight = FontWeight.SemiBold)
            Text("Supermercado: ${entry.supermarket}", style = MaterialTheme.typography.bodySmall)
            Text("Precio: %.2f €".format(entry.price), style = MaterialTheme.typography.bodySmall)
            Text("Código: ${entry.barcode}", style = MaterialTheme.typography.bodySmall)
            Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                if (entry.isSynced) "Estado: sincronizado" else "Estado: pendiente de sync",
                style = MaterialTheme.typography.labelSmall,
                color = if (entry.isSynced) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AssistChip(
                    onClick = onAddPrice,
                    label = { Text("Añadir precio", style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, Modifier.size(16.dp)) },
                    colors = AssistChipDefaults.assistChipColors(leadingIconContentColor = MaterialTheme.colorScheme.primary)
                )

                AssistChip(
                    onClick = onEdit,
                    label = { Text("Editar", style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, Modifier.size(16.dp)) },
                    colors = AssistChipDefaults.assistChipColors(leadingIconContentColor = MaterialTheme.colorScheme.primary)
                )

                AssistChip(
                    onClick = onSync,
                    enabled = !entry.isSynced,
                    label = { Text(if (entry.isSynced) "Sync ✓" else "Sync", style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, Modifier.size(16.dp)) },
                    colors = AssistChipDefaults.assistChipColors(
                        leadingIconContentColor = Color(0xFF2E7D32),
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                AssistChip(
                    onClick = onDelete,
                    label = { Text("Borrar", style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, Modifier.size(16.dp)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        labelColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    border = null
                )
            }
        }
    }
}
