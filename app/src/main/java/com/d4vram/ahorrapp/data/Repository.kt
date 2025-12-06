package com.d4vram.ahorrapp.data

import android.content.Context
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

@Serializable
data class SupabasePriceEntry(
    val barcode: String,
    val supermarket: String,
    val price: Double,
    @SerialName("product_name") val productName: String? = null,
    val brand: String? = null,
    @SerialName("more_info") val moreInfo: String? = null,
    val nickname: String? = null // Campo nuevo para la firma del autor
)

@Serializable
data class DeviceLicense(
    @SerialName("device_id") val deviceId: String,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("nickname") val nickname: String? = null,
    @SerialName("last_used_at") val lastUsedAt: String? = null
)

class Repository(context: Context) {

    // Cliente Supabase conectado a tu proyecto real
    private val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Postgrest)
    }

    private val openFoodApi = Retrofit.Builder()
        .baseUrl("https://world.openfoodfacts.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenFoodApiService::class.java)

    private val db = AppDatabase.getInstance(context)
    private val priceDao = db.priceDao()

    suspend fun postPrice(
        barcode: String,
        supermarket: String,
        price: Double,
        productName: String?,
        brand: String?,
        moreInfo: String?,
        nickname: String? // Recibimos el autor
    ): Boolean {
        var remoteSuccess = true
        runCatching {
            supabase.from("prices").insert(
                SupabasePriceEntry(
                    barcode = barcode,
                    supermarket = supermarket,
                    price = price,
                    productName = productName,
                    brand = brand,
                    moreInfo = moreInfo,
                    nickname = nickname
                )
            )
        }.onFailure { 
            it.printStackTrace()
            android.util.Log.e("SyncError", "Error uploading price: ${it.message}", it)
            remoteSuccess = false 
        }

        priceDao.insert(
            PriceEntryEntity(
                barcode = barcode,
                productName = productName,
                supermarket = supermarket,
                price = price,
                timestamp = System.currentTimeMillis()
            )
        )

        return remoteSuccess
    }

    suspend fun fetchProduct(barcode: String): ProductInfo? {
        val response = openFoodApi.fetchProduct(barcode)

        if (response.status != 1 || response.product == null) return null

        val name = response.product.productName?.trim().orEmpty()
        val brand = response.product.brands?.split(",")?.firstOrNull()?.trim()
        val imageUrl = response.product.imageUrl

        if (name.isEmpty()) return null

        return ProductInfo(name = name, brand = brand, imageUrl = imageUrl)
    }

    fun observePriceHistory(): Flow<List<PriceEntryEntity>> = priceDao.observeAll()

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
}
