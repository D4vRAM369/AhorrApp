package com.d4vram.ahorrapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
private data class BackupEntry(
    val id: Long,
    val barcode: String?,
    val productName: String?,
    val supermarket: String,
    val price: Double,
    val timestamp: Long
)

@Serializable
private data class BackupData(
    val backupDate: Long,
    val appVersion: String,
    val totalEntries: Int,
    val entries: List<BackupEntry>
)

class BackupWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Obtener datos de la base de datos
            val database = com.d4vram.ahorrapp.data.AppDatabase.getInstance(applicationContext)
            val entries: List<com.d4vram.ahorrapp.data.PriceEntryEntity> = database.priceDao().getAllPrices().first()

            if (entries.isEmpty()) {
                // No hay datos para backup, pero no es error
                return Result.success()
            }

            // Crear estructura de backup
            val backupData = BackupData(
                backupDate = System.currentTimeMillis(),
                appVersion = "1.2", // TODO: obtener de BuildConfig
                totalEntries = entries.size,
                entries = entries.map { entry ->
                    BackupEntry(
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
            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            val fileName = "ahorrapp_backup_$timestamp.json"

            // Guardar en directorio de backups (external files dir para que sea accesible)
            val backupDir = File(applicationContext.getExternalFilesDir(null), "backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val backupFile = File(backupDir, fileName)
            backupFile.writeText(jsonString)

            // Limpiar backups antiguos (mantener solo los últimos 10)
            cleanupOldBackups(backupDir)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun cleanupOldBackups(backupDir: File) {
        try {
            val backupFiles = backupDir.listFiles { file ->
                file.name.startsWith("ahorrapp_backup_") && file.name.endsWith(".json")
            }?.toList()?.sortedByDescending { it.lastModified() } ?: emptyList()

            if (backupFiles.size > 10) {
                backupFiles.drop(10).forEach { file -> file.delete() }
            }
        } catch (e: Exception) {
            // Ignorar errores de limpieza
        }
    }
}