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
            // Crear canal de notificaciones si es necesario
            createNotificationChannel()

            // Obtener instancia del repositorio
            val repository = Repository(context)

            // Verificar alertas de precio
            val alertsToNotify = repository.checkPriceAlerts()

            // Enviar notificaciones para cada alerta
            alertsToNotify.forEach { (alert, price, dropPercentage) ->
                sendPriceAlertNotification(alert.barcode, price.productName ?: "Producto", price.price, dropPercentage)

                // Marcar que se envió la alerta
                repository.updateLastAlertTime(alert.deviceId, alert.barcode)
            }

            androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            androidx.work.ListenableWorker.Result.failure()
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
        val message = "$productName ahora cuesta ${String.format("%.2f", newPrice)}€ (-${String.format("%.1f", dropPercentage)}%)"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_ahorrapp_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // El usuario no ha concedido permisos de notificaciones
            e.printStackTrace()
        }
    }
}