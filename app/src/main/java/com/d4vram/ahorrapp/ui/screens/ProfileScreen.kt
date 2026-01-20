package com.d4vram.ahorrapp.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.d4vram.ahorrapp.data.PriceEntryEntity
import com.d4vram.ahorrapp.viewmodel.TpvViewModel
import com.d4vram.ahorrapp.viewmodel.rememberTpvViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.io.File

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import android.Manifest
import android.os.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.core.app.NotificationManagerCompat
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.Serializable
import androidx.compose.ui.platform.LocalContext
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: TpvViewModel
) {
    val context = LocalContext.current
    val history by viewModel.observeHistory().collectAsState(initial = emptyList())
    val savedNickname by viewModel.currentNickname.collectAsState()
    var username by remember(savedNickname) { mutableStateOf(savedNickname) } 
    val scope = rememberCoroutineScope()
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
                .fillMaxSize(),
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
                val areNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

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
                                    val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                        putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    }
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Text(
                                    "Activar Notificaciones",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
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
                        exportJson(context, history)
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
                        createManualBackup(context, viewModel)
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
                onClick = { shareCsv(context, history) },
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

// Función para exportar datos en formato JSON
private fun exportJson(context: Context, data: List<PriceEntryEntity>) {
    try {
        // Verificar si hay datos para exportar
        if (data.isEmpty()) {
            Toast.makeText(context, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear estructura de datos serializable
        val exportData = ExportData(
            exportDate = System.currentTimeMillis(),
            appVersion = "1.1",
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

        // Convertir a JSON
        val jsonString = Json.encodeToString(exportData)

        // Guardar en archivo
        val file = File(context.cacheDir, "historial_precios_ahorrapp.json")
        file.writeText(jsonString)

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Historial de Precios - AhorrApp (JSON)")
            putExtra(Intent.EXTRA_TEXT, "Archivo JSON con mi historial de precios escaneados.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir datos JSON con..."))

        Toast.makeText(context, "Datos exportados correctamente", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error al exportar datos: ${e.message}", Toast.LENGTH_LONG).show()
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

// Función para crear backup manual
private fun createManualBackup(context: Context, viewModel: TpvViewModel) {
    // Usar la misma lógica que exportJson pero con nombre de backup
    val data = viewModel.observeHistory() // Esto es Flow, necesitamos collect

    // Como es manual, ejecutar en coroutine
    kotlinx.coroutines.GlobalScope.launch {
        try {
            val history = viewModel.observeHistory().first() // Obtener datos actuales

            if (history.isEmpty()) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    Toast.makeText(context, "No hay datos para backup", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            // Crear estructura de backup
            val backupData = ExportData(
                exportDate = System.currentTimeMillis(),
                appVersion = "1.2",
                totalEntries = history.size,
                entries = history.map { entry: PriceEntryEntity ->
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

            // Convertir a JSON
            val jsonString = Json.encodeToString(backupData)

            // Crear nombre de archivo con timestamp
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", java.util.Locale.getDefault())
            val timestamp = dateFormat.format(java.util.Date())
            val fileName = "ahorrapp_manual_backup_$timestamp.json"

            // Guardar en cache por ahora (para compartir)
            val file = File(context.cacheDir, fileName)
            file.writeText(jsonString)

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Backup Manual - AhorrApp")
                putExtra(Intent.EXTRA_TEXT, "Backup manual de mis datos de precios.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                context.startActivity(Intent.createChooser(intent, "Compartir backup manual con..."))
                Toast.makeText(context, "Backup manual creado", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                Toast.makeText(context, "Error al crear backup: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

private fun shareCsv(context: Context, data: List<PriceEntryEntity>) {
    // Verificar si hay datos para exportar
    if (data.isEmpty()) {
        Toast.makeText(context, "No hay datos para exportar", Toast.LENGTH_SHORT).show()
        return
    }

    val csvHeader = "ID,Barcode,Name,Supermarket,Price,Date\n"
    val csvBody = data.joinToString("\n") {
        "${it.id},${it.barcode ?: ""},\"${it.productName ?: ""}\",\"${it.supermarket}\",${it.price},${it.timestamp}"
    }
    val csvContent = csvHeader + csvBody

    val file = File(context.cacheDir, "historial_precios.csv")
    file.writeText(csvContent)

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Historial de Precios - AhorrApp")
        putExtra(Intent.EXTRA_TEXT, "Adjunto mi historial de precios escaneados.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir historial con..."))
}
