package com.d4vram.ahorrapp.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Repository(context: Context) {

    private val backendApi = Retrofit.Builder()
        .baseUrl("https://TU_BACKEND.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

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
            backendApi.uploadPrice(
                PricePayload(
                    barcode = barcode,
                    supermarket = supermarket,
                    price = price,
                    timestamp = System.currentTimeMillis(),
                    productName = productName,
                    brand = brand,
                    moreInfo = moreInfo
                )
            )
        }.onFailure { remoteSuccess = false }

        priceDao.insert(
            PriceEntryEntity(
                barcode = barcode,
                productName = productName, // Local DB entity might need update too, but keeping minimal for now
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

        if (name.isEmpty()) return null

        return ProductInfo(name = name, brand = brand)
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
}
