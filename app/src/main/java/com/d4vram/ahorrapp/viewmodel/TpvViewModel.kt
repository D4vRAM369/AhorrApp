package com.d4vram.ahorrapp.viewmodel


import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d4vram.ahorrapp.data.PriceEntryEntity
import com.d4vram.ahorrapp.data.ProductInfo
import com.d4vram.ahorrapp.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TpvViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository(application.applicationContext)

    var productState by mutableStateOf(ProductLookupState())
        private set

    fun sendPrice(barcode: String, supermarket: String, price: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentProduct = productState.product
            runCatching {
                repo.postPrice(
                    barcode = barcode,
                    supermarket = supermarket,
                    price = price,
                    productName = currentProduct?.name,
                    brand = currentProduct?.brand,
                    moreInfo = currentProduct?.moreInfo
                )
            }
        }
    }

    fun fetchProduct(barcode: String) {
        productState = productState.copy(
            isLoading = true,
            error = null,
            product = null
        )

        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching { repo.fetchProduct(barcode) }
            productState = result.fold(
                onSuccess = { product ->
                    if (product == null) {
                        ProductLookupState(
                            isLoading = false,
                            product = null,
                            error = "Producto no encontrado"
                        )
                    } else {
                        ProductLookupState(
                            isLoading = false,
                            product = product,
                            error = null
                        )
                    }
                },
                onFailure = { e ->
                    ProductLookupState(
                        isLoading = false,
                        product = null,
                        error = e.message ?: "No se pudo recuperar el producto"
                    )
                }
            )
        }
    }

    fun observeHistory(): Flow<List<PriceEntryEntity>> = repo.observePriceHistory()

    fun overrideProduct(productInfo: ProductInfo) {
        productState = ProductLookupState(
            isLoading = false,
            product = productInfo,
            error = null
        )
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deletePrice(id)
        }
    }

    fun updateEntry(entry: PriceEntryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updatePrice(entry)
        }
    }

    fun syncEntry(entry: PriceEntryEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repo.postPrice(
                barcode = entry.barcode,
                supermarket = entry.supermarket,
                price = entry.price,
                productName = entry.productName,
                brand = null, // Entry doesn't store brand yet, maybe later
                moreInfo = null // Entry doesn't store moreInfo yet
            )
            launch(Dispatchers.Main) {
                onResult(success)
            }
        }
    }

    fun importCsv(uri: android.net.Uri, contentResolver: android.content.ContentResolver, onResult: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val inputStream = contentResolver.openInputStream(uri)
                val reader = java.io.BufferedReader(java.io.InputStreamReader(inputStream))
                val entries = mutableListOf<PriceEntryEntity>()
                
                reader.useLines { lines ->
                    lines.drop(1).forEach { line -> // Skip header
                        val parts = line.split(",") // Basic parsing, ideally use a standardized CSV lib
                        if (parts.size >= 5) {
                            // Quick fix for parsing logic to match export
                            // "ID,Barcode,Name,Supermarket,Price,Date"
                            // NOTE: Basic csv split breaks on commas inside quotes. 
                            // For this MVP we assume simple data or refined regex logic if needed.
                            // For simplicity/robustness in MVP without external libs:
                            try {
                                val barcode = parts[1]
                                val name = parts[2].replace("\"", "")
                                val market = parts[3].replace("\"", "")
                                val price = parts[4].toDoubleOrNull() ?: 0.0
                                val timestamp = parts.getOrNull(5)?.toLongOrNull() ?: System.currentTimeMillis()
                                
                                entries.add(
                                    PriceEntryEntity(
                                        barcode = barcode,
                                        productName = name,
                                        supermarket = market,
                                        price = price,
                                        timestamp = timestamp
                                    )
                                )
                            } catch (e: Exception) {
                                // Skip malformed line
                            }
                        }
                    }
                }
                repo.importCsvData(entries)
                entries.size
            }.onSuccess { count ->
                launch(Dispatchers.Main) { onResult(count) }
            }.onFailure {
                launch(Dispatchers.Main) { onResult(-1) }
            }
        }
    }

    companion object {
        fun provideFactory(app: Application): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(TpvViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return TpvViewModel(app) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: \$modelClass")
                }
            }
    }

}

data class ProductLookupState(
    val isLoading: Boolean = false,
    val product: ProductInfo? = null,
    val error: String? = null
)
