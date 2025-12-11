package com.d4vram.ahorrapp.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.d4vram.ahorrapp.R
import com.d4vram.ahorrapp.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PriceAlertWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "price_alerts"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        return try {
            // Verificar si las notificaciones están habilitadas
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                // Si no están habilitadas, no hacer nada
                return androidx.work.ListenableWorker.Result.success()
            }

            // Crear canal de notificaciones si es necesario
            createNotificationChannel()

            // Obtener instancia del repositorio
            val repository = Repository(context)

            // Verificar alertas de precio
            val alertsToNotify = repository.checkPriceAlerts()

            if (alertsToNotify.isNotEmpty()) {
                // Enviar notificaciones para cada alerta
                alertsToNotify.forEach { (alert, price, dropPercentage) ->
                    sendPriceAlertNotification(alert.barcode, price.productName ?: "Producto", price.price, dropPercentage)

                    // Marcar que se envió la alerta
                    repository.updateLastAlertTime(alert.deviceId, alert.barcode)
                }
            }

            androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Reintentar en caso de error temporal
            androidx.work.ListenableWorker.Result.retry()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alertas de Precios"
            val descriptionText = "Notificaciones cuando bajan los precios de tus productos favoritos"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendPriceAlertNotification(barcode: String, productName: String, newPrice: Double, dropPercentage: Double) {
        val notificationId = barcode.hashCode() // ID único por producto

        val title = "¡Precio bajado! 📉"
        val shortMessage = "$productName: ${String.format("%.2f", newPrice)}€"
        val fullMessage = "$productName ahora cuesta ${String.format("%.2f", newPrice)}€ " +
                         "(-${String.format("%.1f", dropPercentage)}% de descuento)"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_ahorrapp_foreground)
            .setContentTitle(title)
            .setContentText(shortMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(fullMessage))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // El usuario no ha concedido permisos de notificaciones
            android.util.Log.w("PriceAlertWorker", "No se pudieron enviar notificaciones: permisos no concedidos")
        } catch (e: Exception) {
            android.util.Log.e("PriceAlertWorker", "Error al enviar notificación", e)
        }
    }
}