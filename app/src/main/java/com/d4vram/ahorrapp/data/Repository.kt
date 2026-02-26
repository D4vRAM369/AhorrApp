package com.d4vram.ahorrapp.data
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import android.content.Context
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.d4vram.ahorrapp.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Removed unused kotlinx.datetime imports
import io.github.jan.supabase.postgrest.query.Order

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

// Función de extensión para obtener Device ID
fun Context.getDeviceId(): String {
    return Settings.Secure.getString(
        this.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "unknown_device"
}

@Serializable
data class SupabasePriceEntry(
    val id: Long? = null, // Added to avoid strict parsing errors
    val barcode: String,
    val supermarket: String,
    val price: Double,
    @SerialName("product_name") val productName: String? = null,
    val brand: String? = null,
    @SerialName("more_info") val moreInfo: String? = null,
    @SerialName("device_id") val deviceId: String? = null, // Nuevo para v1.1
    val nickname: String? = null, // Campo nuevo para la firma del autor
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class SupabaseProduct(
    val barcode: String,
    val name: String,
    val brand: String? = null,
    @SerialName("more_info") val moreInfo: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class DeviceLicense(
    @SerialName("device_id") val deviceId: String,
    val isActive: Boolean = true,
    @SerialName("nickname") val nickname: String? = null,
    @SerialName("last_used_at") val lastUsedAt: String? = null
)

@Serializable
data class UserFavorite(
    val id: Long? = null,
    @SerialName("device_id") val deviceId: String,
    @SerialName("barcode") val barcode: String,
    @SerialName("product_name") val productName: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class PriceAlert(
    val id: Long? = null,
    @SerialName("device_id") val deviceId: String,
    @SerialName("barcode") val barcode: String,
    @SerialName("target_price") val targetPrice: Double? = null,
    @SerialName("alert_percentage") val alertPercentage: Double = 10.0,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("last_alert_at") val lastAlertAt: String? = null
)

class Repository(context: Context) {

    // Cliente Supabase conectado a tu proyecto real
    // ✅ Claves obtenidas desde código nativo C++ (SecureKeys)
    private val supabase = createSupabaseClient(
        supabaseUrl = SecureKeys.getSupabaseUrl(),
        supabaseKey = SecureKeys.getSupabaseKey()
    ) {
        // Configuración tolerante a fallos para el JSON
        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true // ¡CRUCIAL! Ignora campos extra que mande Supabase
            encodeDefaults = false
        })

        install(Postgrest)
    }

    init {
        // Log removido - las claves ya no están en BuildConfig
        // Log.e("SupabaseConfig", "URL: ${BuildConfig.SUPABASE_URL}")
    }

    private val openFoodApi = Retrofit.Builder()
        .baseUrl("https://world.openfoodfacts.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenFoodApiService::class.java)

    private val db = AppDatabase.getInstance(context)
    private val priceDao = db.priceDao()
    private val productDao = db.productDao()

    // Función para enviar eventos de analytics
    suspend fun sendAnalyticsEvent(
        deviceId: String,
        eventType: String,
        eventData: Map<String, Any> = emptyMap(),
        sessionId: String? = null
    ) {
        try {
            supabase.from("analytics_events").insert(
                mapOf(
                    "device_id" to deviceId,
                    "event_type" to eventType,
                    "event_data" to eventData,
                    "session_id" to sessionId
                )
            )
        } catch (e: Exception) {
            // Silenciar errores de analytics para no afectar UX principal
            android.util.Log.d("Repository", "Analytics event failed: ${e.message}")
        }
    }

    suspend fun postPrice(
        barcode: String,
        supermarket: String,
        price: Double,
        productName: String?,
        brand: String?,
        moreInfo: String?,
        deviceId: String, // Nuevo parámetro requerido para v1.1
        nickname: String?, // Recibimos el autor
        saveLocalCopy: Boolean = true
    ): Boolean {
        var remoteSuccess = true
        runCatching {
            if (!productName.isNullOrBlank()) {
                val productSynced = uploadProductToSupabase(
                    SupabaseProduct(
                        barcode = barcode,
                        name = productName,
                        brand = brand,
                        moreInfo = moreInfo
                    )
                )
                if (!productSynced) {
                    error("No se pudo sincronizar el producto en Supabase")
                }
            }

            supabase.from("prices").insert(
                SupabasePriceEntry(
                    barcode = barcode,
                    supermarket = supermarket,
                    price = price,
                    productName = productName,
                    brand = brand,
                    moreInfo = moreInfo,
                    deviceId = deviceId,
                    nickname = nickname
                )
            )

            // Enviar evento de analytics si la subida fue exitosa
            sendAnalyticsEvent(
                deviceId = deviceId,
                eventType = "price_reported",
                eventData = mapOf(
                    "barcode" to barcode,
                    "supermarket" to supermarket,
                    "price" to price,
                    "has_product_name" to (productName != null)
                )
            )
        }.onFailure {
            it.printStackTrace()
            android.util.Log.e("SyncError", "Error uploading price: ${it.message}", it)
            remoteSuccess = false
        }

        if (saveLocalCopy) {
            priceDao.insert(
                PriceEntryEntity(
                    barcode = barcode,
                    productName = productName,
                    supermarket = supermarket,
                    price = price,
                    timestamp = System.currentTimeMillis(),
                    isSynced = remoteSuccess
                )
            )
        }

        return remoteSuccess
    }

    suspend fun getLatestPriceForBarcode(
        barcode: String,
        supermarket: String
    ): Result<SupabasePriceEntry?> {
        return runCatching {
            val result = supabase.from("prices").select {
                filter {
                    eq("barcode", barcode)
                    eq("supermarket", supermarket)
                }
                order("created_at", Order.DESCENDING)
                limit(1)
            }.decodeList<SupabasePriceEntry>().firstOrNull()
            result
        }
    }

    suspend fun getAllPricesForBarcode(barcode: String): Result<List<SupabasePriceEntry>> {
        return runCatching {
            supabase.from("prices").select {
                filter {
                    eq("barcode", barcode)
                }
                order("created_at", Order.DESCENDING)
            }.decodeList<SupabasePriceEntry>()
        }
    }

    suspend fun fetchProduct(barcode: String): ProductInfo? {
        // 1. Primero buscar en nuestra base de datos local de productos
        val localProduct = productDao.getProductByBarcode(barcode)
        if (localProduct != null) {
            return ProductInfo(
                name = localProduct.name,
                brand = localProduct.brand,
                moreInfo = localProduct.moreInfo,
                imageUrl = localProduct.imageUrl
            )
        }

        // 2. Intentar buscar en Supabase (Centralizado)
        val supabaseProduct = runCatching {
            supabase.from("products").select {
                filter { eq("barcode", barcode) }
                limit(1)
            }.decodeSingleOrNull<SupabaseProduct>()
        }.getOrNull()

        if (supabaseProduct != null) {
            // Guardar en local para futuras búsquedas rápidas
            saveLocalProduct(
                barcode = supabaseProduct.barcode,
                name = supabaseProduct.name,
                brand = supabaseProduct.brand,
                moreInfo = supabaseProduct.moreInfo,
                imageUrl = supabaseProduct.imageUrl
            )
            return ProductInfo(
                name = supabaseProduct.name,
                brand = supabaseProduct.brand,
                moreInfo = supabaseProduct.moreInfo,
                imageUrl = supabaseProduct.imageUrl
            )
        }

        // 3. Intentar con OpenFoodFacts
        val response = try {
            openFoodApi.fetchProduct(barcode)
        } catch (e: Exception) {
            null
        }

        if (response != null && response.status == 1 && response.product != null) {
            val name = response.product.productName?.trim().orEmpty()
            if (name.isNotEmpty()) {
                val brand = response.product.brands?.split(",")?.firstOrNull()?.trim()
                val imageUrl = response.product.imageUrl
                
                val productInfo = ProductInfo(name = name, brand = brand, imageUrl = imageUrl)
                
                // Subir a Supabase para que otros aprovechen esta info
                uploadProductToSupabase(SupabaseProduct(
                    barcode = barcode,
                    name = name,
                    brand = brand,
                    imageUrl = imageUrl
                ))
                
                return productInfo
            }
        }

        // 4. Si falla, intentar buscar en Supabase (TABLA PRECIOS)
        // Buscamos si ya existe algún precio registrado para este código, y cogemos su nombre
        return runCatching {
            val existingEntry = supabase.from("prices").select {
                filter {
                    eq("barcode", barcode)
                }
                order("created_at", Order.DESCENDING)
                limit(1)
            }.decodeList<SupabasePriceEntry>().firstOrNull()

            if (existingEntry != null && !existingEntry.productName.isNullOrBlank()) {
                  val info = ProductInfo(
                    name = existingEntry.productName,
                    brand = existingEntry.brand,
                    moreInfo = existingEntry.moreInfo
                )
                
                // También lo subimos a la tabla central de productos si lo encontramos aquí
                uploadProductToSupabase(SupabaseProduct(
                    barcode = barcode,
                    name = existingEntry.productName,
                    brand = existingEntry.brand,
                    moreInfo = existingEntry.moreInfo
                ))
                
                info
            } else {
                null
            }
        }.getOrNull()
    }

    fun observePriceHistory(): Flow<List<PriceEntryEntity>> = priceDao.observeAll()

    fun observePendingSyncCount(): Flow<Int> = priceDao.observePendingSyncCount()

    suspend fun getUnsyncedEntries(): List<PriceEntryEntity> = priceDao.getUnsyncedEntries()

    suspend fun markPriceSynced(id: Long) {
        priceDao.markSynced(id)
    }

    suspend fun deletePrice(id: Long) {
        priceDao.delete(id)
    }

    suspend fun updatePrice(entry: PriceEntryEntity) {
        priceDao.update(entry)
    }

    suspend fun importCsvData(entries: List<PriceEntryEntity>) {
        // Room doesn't have bulk insert in this Dao yet, loop or add @Insert(List)
        // For safety and simplicity in this sprint, we loop. Ideally we update Dao.
        entries.forEach { priceDao.insert(it) }
    }

    fun searchProducts(query: String): Flow<List<String>> = priceDao.searchProductNames(query)

    fun getProductPrices(productName: String): Flow<List<PriceEntryEntity>> = priceDao.getPricesForProduct(productName)

    fun getAllPrices(): Flow<List<PriceEntryEntity>> = priceDao.getAllPrices()

    suspend fun checkDeviceLicense(deviceId: String): Pair<Boolean, String?> {
        // Return Pair(isActive, nickname)
        // 1. Try to fetch existing
        var isActive = true
        var nickname: String? = null
        
        // Use ISO 8601 format compatible with Timestamptz
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val now = sdf.format(java.util.Date())

        val result = runCatching {
            // Upsert: We want to insert if new, update last_used if exists.
            // But we don't want to reset 'is_active' to true if it was false.
            // So we fetch first.
            val existing = supabase.from("app_licenses").select {
                filter {
                    eq("device_id", deviceId)
                }
            }.decodeSingleOrNull<DeviceLicense>()

            if (existing != null) {
                isActive = existing.isActive
                nickname = existing.nickname
                
                // Update stats
                supabase.from("app_licenses").update(
                    {
                        set("last_used_at", now)
                    }
                ) {
                    filter { eq("device_id", deviceId) }
                }
            } else {
                // Insert new
                supabase.from("app_licenses").insert(
                    DeviceLicense(deviceId = deviceId, isActive = true, lastUsedAt = now)
                )
                isActive = true
            }
        }
        
        if (result.isFailure) {
            result.exceptionOrNull()?.printStackTrace()
            // Fail open or closed? If network fails, usually allow access but maybe warn.
            // User asked for "License system", usually implies enforcement.
            // But for MVP, let's return true (allow) if network fails, to avoid bricking offline users.
            // UNLESS the user explicitly wants strict online check. 
            // "probablemente por que hay una actualización pendiente o por algún error" -> implies blocking on specific error/state.
            // Let's assume default true if network error, but strict false if explicit false in DB.
        }

        return Pair(isActive, nickname)
    }

    suspend fun updateNickname(deviceId: String, name: String) {
        runCatching {
            supabase.from("app_licenses").update(
                {
                    set("nickname", name)
                }
            ) {
                filter { eq("device_id", deviceId) }
            }
        }
    }

    // --- FUNCIONES PARA SISTEMA DE ALERTAS ---

    suspend fun addToFavorites(deviceId: String, barcode: String, productName: String?): Boolean {
        return runCatching {
            supabase.from("user_favorites").insert(
                UserFavorite(
                    deviceId = deviceId,
                    barcode = barcode,
                    productName = productName
                )
            )

            // Enviar evento de analytics
            sendAnalyticsEvent(
                deviceId = deviceId,
                eventType = "favorite_added",
                eventData = mapOf(
                    "barcode" to barcode as Any,
                    "product_name" to (productName ?: "") as Any
                )
            )

            true
        }.getOrDefault(false)
    }

    suspend fun removeFromFavorites(deviceId: String, barcode: String): Boolean {
        return runCatching {
            supabase.from("user_favorites").delete {
                filter {
                    eq("device_id", deviceId)
                    eq("barcode", barcode)
                }
            }

            // Enviar evento de analytics
            sendAnalyticsEvent(
                deviceId = deviceId,
                eventType = "favorite_removed",
                eventData = mapOf("barcode" to barcode)
            )

            true
        }.getOrDefault(false)
    }

    suspend fun getUserFavorites(deviceId: String): List<UserFavorite> {
        return runCatching {
            supabase.from("user_favorites").select {
                filter { eq("device_id", deviceId) }
            }.decodeList<UserFavorite>()
        }.getOrDefault(emptyList())
    }

    suspend fun isProductFavorite(deviceId: String, barcode: String): Boolean {
        return runCatching {
            val result = supabase.from("user_favorites").select {
                filter {
                    eq("device_id", deviceId)
                    eq("barcode", barcode)
                }
            }.decodeSingleOrNull<UserFavorite>()
            result != null
        }.getOrDefault(false)
    }

    suspend fun createOrUpdatePriceAlert(
        deviceId: String,
        barcode: String,
        targetPrice: Double? = null,
        alertPercentage: Double = 10.0
    ): Boolean {
        return runCatching {
            // Primero intentamos actualizar si ya existe
            val existing = supabase.from("price_alerts").select {
                filter {
                    eq("device_id", deviceId)
                    eq("barcode", barcode)
                }
            }.decodeSingleOrNull<PriceAlert>()

            val isUpdate = existing != null

            if (existing != null) {
                // Update existing
                supabase.from("price_alerts").update(
                    {
                        set("target_price", targetPrice)
                        set("alert_percentage", alertPercentage)
                        set("is_active", true)
                    }
                ) {
                    filter {
                        eq("device_id", deviceId)
                        eq("barcode", barcode)
                    }
                }
            } else {
                // Create new
                supabase.from("price_alerts").insert(
                    PriceAlert(
                        deviceId = deviceId,
                        barcode = barcode,
                        targetPrice = targetPrice,
                        alertPercentage = alertPercentage
                    )
                )
            }

            // Enviar evento de analytics
            sendAnalyticsEvent(
                deviceId = deviceId,
                eventType = if (isUpdate) "alert_updated" else "alert_created",
                eventData = mapOf(
                    "barcode" to barcode as Any,
                    "target_price" to (targetPrice ?: 0.0) as Any,
                    "alert_percentage" to alertPercentage as Any,
                    "is_update" to isUpdate as Any
                )
            )

            true
        }.getOrDefault(false)
    }

    suspend fun getUserAlerts(deviceId: String): List<PriceAlert> {
        return runCatching {
            supabase.from("price_alerts").select {
                filter {
                    eq("device_id", deviceId)
                    eq("is_active", true)
                }
            }.decodeList<PriceAlert>()
        }.getOrDefault(emptyList())
    }

    suspend fun updateLastAlertTime(deviceId: String, barcode: String) {
        runCatching {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val now = sdf.format(java.util.Date())

            supabase.from("price_alerts").update(
                {
                    set("last_alert_at", now)
                }
            ) {
                filter {
                    eq("device_id", deviceId)
                    eq("barcode", barcode)
                }
            }
        }
    }

    suspend fun checkPriceAlerts(): List<Triple<PriceAlert, SupabasePriceEntry, Double>> {
        // Esta función busca alertas que necesiten notificación
        // Devuelve: (Alert, LatestPrice, PriceDropPercentage)

        val alertsToNotify = mutableListOf<Triple<PriceAlert, SupabasePriceEntry, Double>>()

        try {
            // Obtener todas las alertas activas
            val allAlerts = supabase.from("price_alerts").select {
                filter { eq("is_active", true) }
            }.decodeList<PriceAlert>()

            for (alert in allAlerts) {
                // Buscar el precio más reciente para este producto
                val latestPrice = supabase.from("prices").select {
                    filter { eq("barcode", alert.barcode) }
                }.decodeList<SupabasePriceEntry>()
                    .minByOrNull { it.createdAt ?: "" } // Más reciente primero

                if (latestPrice != null) {
                    val currentPrice = latestPrice.price

                    // Verificar si cumple con criterios de alerta
                    val shouldAlert = when {
                        // Si hay precio objetivo y el precio actual es menor o igual
                        alert.targetPrice != null && currentPrice <= alert.targetPrice!! -> true
                        // Si hay porcentaje de bajada configurado
                        else -> {
                            // Buscar el precio anterior (penúltimo más reciente)
                            val previousPrices = supabase.from("prices").select {
                                filter { eq("barcode", alert.barcode) }
                            }.decodeList<SupabasePriceEntry>()
                                .sortedByDescending { it.createdAt ?: "" }
                                .drop(1) // Saltar el más reciente

                            if (previousPrices.isNotEmpty()) {
                                val previousPrice = previousPrices.first().price
                                val dropPercentage = ((previousPrice - currentPrice) / previousPrice) * 100
                                dropPercentage >= alert.alertPercentage
                            } else {
                                false // No hay precio anterior para comparar
                            }
                        }
                    }

                    if (shouldAlert) {
                        val dropPercentage = if (alert.targetPrice != null) {
                            ((alert.targetPrice!! - currentPrice) / alert.targetPrice!!) * 100
                        } else {
                            // Calcular basado en último precio conocido
                            val previousPrices = supabase.from("prices").select {
                                filter { eq("barcode", alert.barcode) }
                            }.decodeList<SupabasePriceEntry>()
                                .sortedByDescending { it.createdAt ?: "" }
                                .drop(1)

                            if (previousPrices.isNotEmpty()) {
                                val previousPrice = previousPrices.first().price
                                ((previousPrice - currentPrice) / previousPrice) * 100
                            } else {
                                0.0
                            }
                        }

                        alertsToNotify.add(Triple(alert, latestPrice, dropPercentage))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return alertsToNotify
    }

    // --- FUNCIONES PARA PRODUCTOS LOCALES ---

    suspend fun saveLocalProduct(
        barcode: String,
        name: String,
        brand: String? = null,
        moreInfo: String? = null,
        imageUrl: String? = null
    ) {
        val product = ProductEntity(
            barcode = barcode,
            name = name,
            brand = brand,
            moreInfo = moreInfo,
            imageUrl = imageUrl
        )
        productDao.insert(product)
        
        // Sincronizar con Supabase
        uploadProductToSupabase(SupabaseProduct(
            barcode = barcode,
            name = name,
            brand = brand,
            moreInfo = moreInfo,
            imageUrl = imageUrl
        ))
    }

    suspend fun uploadProductToSupabase(product: SupabaseProduct): Boolean {
        return runCatching {
            // Usamos upsert para que si ya existe, se actualice la info
            supabase.from("products").upsert(product, onConflict = "barcode")
        }.onFailure {
            android.util.Log.e("Repository", "Error uploading product to Supabase: ${it.message}")
        }.isSuccess
    }

    suspend fun getLocalProduct(barcode: String): ProductEntity? {
        return productDao.getProductByBarcode(barcode)
    }

    suspend fun updateLocalProduct(product: ProductEntity) {
        productDao.update(product)
    }

    suspend fun deleteLocalProduct(barcode: String) {
        productDao.deleteByBarcode(barcode)
    }

    fun observeLocalProducts(): Flow<List<ProductEntity>> = productDao.observeAll()

    fun searchLocalProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProducts(query)

    suspend fun getLocalProductCount(): Int = productDao.getProductCount()

    // Funciones helper para métricas de usuario
    suspend fun getUserTotalScans(deviceId: String): Int {
        return runCatching {
            supabase.from("analytics_events")
                .select { filter { eq("device_id", deviceId) } }
                .decodeList<Map<String, Any>>()
                .count { (it["event_type"] as? String) == "price_reported" }
        }.getOrDefault(0)
    }

    suspend fun getUserUniqueProducts(deviceId: String): Int {
        return runCatching {
            supabase.from("prices")
                .select { filter { eq("device_id", deviceId) } }
                .decodeList<Map<String, Any>>()
                .distinctBy { it["barcode"] as? String }
                .size
        }.getOrDefault(0)
    }

    suspend fun getUserTotalContributions(deviceId: String): Int {
        return runCatching {
            supabase.from("prices")
                .select { filter { eq("device_id", deviceId) } }
                .decodeList<Map<String, Any>>()
                .size
        }.getOrDefault(0)
    }

    suspend fun getUserTotalFavorites(deviceId: String): Int {
        return runCatching {
            supabase.from("user_favorites")
                .select { filter { eq("device_id", deviceId) } }
                .decodeList<Map<String, Any>>()
                .size
        }.getOrDefault(0)
    }

    suspend fun getUserTotalAlerts(deviceId: String): Int {
        return runCatching {
            supabase.from("price_alerts")
                .select { filter { eq("device_id", deviceId) } }
                .decodeList<Map<String, Any>>()
                .size
        }.getOrDefault(0)
    }

    suspend fun updateUserAnalytics(
        deviceId: String,
        totalScans: Int,
        uniqueProducts: Int,
        totalContributions: Int,
        totalFavorites: Int,
        totalAlerts: Int
    ) {
        try {
            // Intentar actualizar primero
            val existing = supabase.from("user_analytics")
                .select { filter { eq("device_id", deviceId) } }
                .decodeSingleOrNull<Map<String, Any>>()

            val analyticsData = mapOf(
                "device_id" to deviceId,
                "total_scans" to totalScans,
                "unique_products_scanned" to uniqueProducts,
                "total_prices_reported" to totalContributions,
                "total_favorites_added" to totalFavorites,
                "total_alerts_created" to totalAlerts,
                "updated_at" to "now()"
            )

            if (existing != null) {
                // Update existing
                supabase.from("user_analytics").update(analyticsData) {
                    filter { eq("device_id", deviceId) }
                }
            } else {
                // Insert new
                supabase.from("user_analytics").insert(analyticsData)
            }
        } catch (e: Exception) {
            // Silenciar errores de analytics
            android.util.Log.d("Repository", "Failed to update user analytics: ${e.message}")
        }
    }

    // Funciones para sistema de mensajes push
    suspend fun getUnseenPushMessages(deviceId: String): List<PushMessage> {
        // Por ahora devolver lista vacía - los mensajes se manejarán desde Supabase directamente
        return emptyList()
    }

    suspend fun markMessageViewed(messageId: String, deviceId: String, action: String = "viewed") {
        // Simplemente loggear por ahora
        android.util.Log.d("Repository", "Message $messageId marked as $action by $deviceId")
    }
}
