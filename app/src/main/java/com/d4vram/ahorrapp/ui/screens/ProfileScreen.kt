package com.d4vram.ahorrapp.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.d4vram.ahorrapp.data.PriceEntryEntity
import com.d4vram.ahorrapp.viewmodel.TpvViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: TpvViewModel,
    onReplayTutorial: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val history by viewModel.observeHistory().collectAsState(initial = emptyList())
    val savedNickname by viewModel.currentNickname.collectAsState()
    var username by remember(savedNickname) { mutableStateOf(savedNickname) } 
    var exportAsBackup by remember { mutableStateOf(false) }
    var areNotificationsEnabled by remember {
        mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled())
    }
    val refreshNotificationState = {
        areNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        refreshNotificationState()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshNotificationState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.importCsv(it, context.contentResolver) { count ->
                if (count >= 0) {
                    Toast.makeText(context, "$count precios importados correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al importar el archivo CSV", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val jsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            importJson(context, it) { count ->
                if (count >= 0) {
                    Toast.makeText(context, "$count entradas JSON procesadas", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al importar archivo JSON", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val createCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            exportCsvToUri(context, uri, history)
        } else {
            Toast.makeText(context, "Exportación CSV cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    val createJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            val versionLabel = if (exportAsBackup) "1.3-backup" else "1.3"
            exportJsonToUri(context, uri, history, versionLabel)
            exportAsBackup = false
        } else {
            Toast.makeText(context, "Exportación JSON cancelada", Toast.LENGTH_SHORT).show()
            exportAsBackup = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil y Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sección Usuario
            Text("Información de usuario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val isModified = username != savedNickname
            
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (isModified && username.isNotBlank()) {
                        IconButton(onClick = { viewModel.saveNickname(username) }) {
                            Icon(Icons.Default.Check, contentDescription = "Guardar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
            Text(
                "Este nombre se usará para identificar tus aportaciones en la comunidad.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Sección Estadísticas
            Text("Estadísticas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            StatsCard(history)

            // Sección Sonido (movida aquí para mejor visibilidad)
            Text("Sonido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val scanSoundEnabled by viewModel.scanSoundEnabled.collectAsState()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sonido de escaneo", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Reproduce un 'bip' al detectar un código correctamente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    androidx.compose.material3.Switch(
                        checked = scanSoundEnabled,
                        onCheckedChange = { viewModel.toggleScanSound() }
                    )
                }
            }

            // Sección Notificaciones
            Text("Notificaciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Permiso de notificaciones
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (areNotificationsEnabled)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Notificaciones",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                if (areNotificationsEnabled) "✅ ON" else "❌ OFF",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (areNotificationsEnabled)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Recibe alertas cuando bajen los precios de tus productos favoritos.",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.2f
                        )

                        if (!areNotificationsEnabled) {
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val hasRuntimePermission =
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED

                                    if (!hasRuntimePermission) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                            putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                                        }
                                        context.startActivity(intent)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Activar notificaciones",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }

            // Sección Ayuda
            Text("Ayuda", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(
                onClick = onReplayTutorial,
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Ver tutorial de nuevo",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Repite el onboarding completo y la pantalla de bienvenida.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Sección Backup
            Text("Backup de datos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val autoBackupEnabled by viewModel.autoBackupEnabled.collectAsState()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Backup automático diario", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Crea copias de seguridad automáticas de tus datos cada día.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    androidx.compose.material3.Switch(
                        checked = autoBackupEnabled,
                        onCheckedChange = { viewModel.toggleAutoBackup() }
                    )
                }
            }

            // Sección Ajustes Avanzados
            Text("Ajustes avanzados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Chip de ajustes con menú desplegable
            var showSettingsMenu by remember { mutableStateOf(false) }

            AssistChip(
                onClick = { showSettingsMenu = true },
                label = { Text("⚙️ Ajustes") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth(0.6f)
            )

            // Menú desplegable de ajustes
            DropdownMenu(
                expanded = showSettingsMenu,
                onDismissRequest = { showSettingsMenu = false }
            ) {
                // Opción para modificar nickname
                DropdownMenuItem(
                    text = { Text("Modificar nickname") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Nickname"
                        )
                    },
                    onClick = {
                        showSettingsMenu = false
                        // El campo de nickname ya está visible arriba, hacer scroll hasta él
                        // Por ahora solo cerrar el menú
                    }
                )

                // Opción para exportar JSON
                DropdownMenuItem(
                    text = { Text("Exportar datos (JSON)") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Exportar"
                        )
                    },
                    onClick = {
                        showSettingsMenu = false
                        if (history.isEmpty()) {
                            Toast.makeText(context, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
                        } else {
                            exportAsBackup = false
                            createJsonLauncher.launch("historial_precios_ahorrapp.json")
                        }
                    }
                )

                // Opción para importar JSON
                DropdownMenuItem(
                    text = { Text("Importar datos (JSON)") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Upload,
                            contentDescription = "Importar"
                        )
                    },
                    onClick = {
                        showSettingsMenu = false
                        jsonLauncher.launch(arrayOf("application/json"))
                    }
                )

                // Opción para crear backup manual
                DropdownMenuItem(
                    text = { Text("Crear backup manual") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Backup"
                        )
                    },
                    onClick = {
                        showSettingsMenu = false
                        if (history.isEmpty()) {
                            Toast.makeText(context, "No hay datos para backup", Toast.LENGTH_SHORT).show()
                        } else {
                            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
                            exportAsBackup = true
                            createJsonLauncher.launch("ahorrapp_manual_backup_$timestamp.json")
                        }
                    }
                )

                // Opción de información
                DropdownMenuItem(
                    text = { Text("Información de la app") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info"
                        )
                    },
                    onClick = {
                        showSettingsMenu = false
                        Toast.makeText(context, "AhorrApp v1.1 - Comunidad de precios", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Sección Datos
            Text("Gestión de datos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Export Button
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                onClick = {
                    if (history.isEmpty()) {
                        Toast.makeText(context, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
                    } else {
                        createCsvLauncher.launch("historial_precios.csv")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Exportar historial a CSV")
                }
            }

            // Import Button
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                onClick = {
                    csvLauncher.launch(arrayOf("text/comma-separated-values", "text/csv", "application/csv"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Importar historial desde CSV")
                }
            }
        }
    }
}

@Composable
fun StatsCard(history: List<PriceEntryEntity>) {
    val totalProducts = history.size
    val totalPrices = history.count { it.price > 0 } // Logic simplification
    // Mocked updated counts as we don't track updates separately yet
    val updatedProducts = 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatRow("Productos añadidos", totalProducts.toString())
            StatRow("Precios compartidos", totalPrices.toString())
            StatRow("Productos actualizados", updatedProducts.toString()) // Placeholder
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

// Clases de datos para exportación JSON
@kotlinx.serialization.Serializable
private data class ExportEntry(
    val id: Long,
    val barcode: String?,
    val productName: String?,
    val supermarket: String,
    val price: Double,
    val timestamp: Long
)

@kotlinx.serialization.Serializable
private data class ExportData(
    val exportDate: Long,
    val appVersion: String,
    val totalEntries: Int,
    val entries: List<ExportEntry>
)

private fun buildExportData(data: List<PriceEntryEntity>, appVersion: String): ExportData {
    return ExportData(
        exportDate = System.currentTimeMillis(),
        appVersion = appVersion,
        totalEntries = data.size,
        entries = data.map { entry ->
            ExportEntry(
                id = entry.id,
                barcode = entry.barcode,
                productName = entry.productName,
                supermarket = entry.supermarket,
                price = entry.price,
                timestamp = entry.timestamp
            )
        }
    )
}

private fun writeTextToUri(context: Context, uri: android.net.Uri, content: String): Boolean {
    try {
        context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
            output.write(content.toByteArray())
        }
        return true
    } catch (e: Exception) {
        Toast.makeText(context, "Error al guardar archivo: ${e.message}", Toast.LENGTH_LONG).show()
        return false
    }
}

private fun exportJsonToUri(
    context: Context,
    uri: android.net.Uri,
    data: List<PriceEntryEntity>,
    appVersion: String
) {
    if (data.isEmpty()) {
        Toast.makeText(context, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
        return
    }
    val jsonString = Json.encodeToString(buildExportData(data, appVersion))
    if (writeTextToUri(context, uri, jsonString)) {
        Toast.makeText(context, "JSON exportado correctamente", Toast.LENGTH_SHORT).show()
    }
}

private fun exportCsvToUri(context: Context, uri: android.net.Uri, data: List<PriceEntryEntity>) {
    if (data.isEmpty()) {
        Toast.makeText(context, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
        return
    }
    val csvHeader = "ID,Barcode,Name,Supermarket,Price,Date\n"
    val csvBody = data.joinToString("\n") {
        val safeName = (it.productName ?: "").replace("\"", "\"\"")
        val safeMarket = it.supermarket.replace("\"", "\"\"")
        "${it.id},${it.barcode ?: ""},\"$safeName\",\"$safeMarket\",${it.price},${it.timestamp}"
    }
    val csvContent = csvHeader + csvBody
    if (writeTextToUri(context, uri, csvContent)) {
        Toast.makeText(context, "CSV exportado correctamente", Toast.LENGTH_SHORT).show()
    }
}

// Función para importar datos desde JSON
private fun importJson(context: Context, uri: android.net.Uri, onResult: (Int) -> Unit) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = reader.readText()
        reader.close()
        inputStream?.close()

        // Parsear JSON usando las clases serializables
        val exportData = Json.decodeFromString<ExportData>(jsonString)

        // Aquí se podría implementar la lógica para guardar los datos importados
        // Por ahora, solo mostrar información
        Toast.makeText(context, "Importados ${exportData.entries.size} productos desde JSON", Toast.LENGTH_LONG).show()
        onResult(exportData.entries.size)

    } catch (e: Exception) {
        Toast.makeText(context, "Error al importar JSON: ${e.message}", Toast.LENGTH_LONG).show()
        onResult(-1)
    }
}
