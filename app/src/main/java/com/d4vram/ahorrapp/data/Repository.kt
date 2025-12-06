package com.d4vram.ahorrapp.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.d4vram.ahorrapp.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabasePriceEntry(
    val barcode: String,
    val supermarket: String,
    val price: Double,
    @SerialName("product_name") val productName: String?,
    val brand: String?,
    @SerialName("more_info") val moreInfo: String?
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
        moreInfo: String?
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
                    moreInfo = moreInfo
                )
            )
        }.onFailure { 
            it.printStackTrace()
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
}
