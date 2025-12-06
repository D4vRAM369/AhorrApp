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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TpvViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository(application.applicationContext)

    var productState by mutableStateOf(ProductLookupState())
        private set

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _currentNickname = MutableStateFlow("Usuario Anónimo")
    val currentNickname: StateFlow<String> = _currentNickname.asStateFlow()
    
    // Store Device ID
    private val deviceId: String by lazy {
        android.provider.Settings.Secure.getString(application.contentResolver, android.provider.Settings.Secure.ANDROID_ID) ?: "unknown_device"
    }

    init {
        checkLicense()
    }

    private fun checkLicense() {
        viewModelScope.launch(Dispatchers.IO) {
            val (isActive, nickname) = repo.checkDeviceLicense(deviceId)
            _isAppLocked.value = !isActive
            if (nickname != null) {
                _currentNickname.value = nickname
            }
        }
    }

    fun saveNickname(newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateNickname(deviceId, newName)
            _currentNickname.value = newName
        }
    }
    
    fun getDeviceIdStr(): String = deviceId

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

    fun addExtraPrice(originalEntry: PriceEntryEntity, supermarket: String, price: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.postPrice(
                barcode = originalEntry.barcode,
                supermarket = supermarket,
                price = price,
                productName = originalEntry.productName,
                brand = null, 
                moreInfo = null
            )
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

    // --- Search & Comparison Logic ---

    var searchQuery by mutableStateOf("")
        private set

    @OptIn(kotlinx.coroutines.FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<String>> = androidx.compose.runtime.snapshotFlow { searchQuery }
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) kotlinx.coroutines.flow.flowOf(emptyList())
            else repo.searchProducts(query)
        }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    var selectedProductName by mutableStateOf<String?>(null)
        private set

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val comparisonPrices: StateFlow<List<PriceEntryEntity>> = androidx.compose.runtime.snapshotFlow { selectedProductName }
        .flatMapLatest { name ->
            if (name == null) kotlinx.coroutines.flow.flowOf(emptyList())
            else repo.getProductPrices(name)
        }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    var isListViewMode by mutableStateOf(false)
        private set

    fun toggleListViewMode() {
        isListViewMode = !isListViewMode
    }

    val allProductsComparison: StateFlow<Map<String, List<PriceEntryEntity>>> = repo.getAllPrices()
        .map { entries -> 
            entries.groupBy { it.productName ?: "Desconocido" }
        }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyMap())

    var selectedProductInfo by mutableStateOf<ProductInfo?>(null)
        private set

    fun updateSearchQuery(query: String) {
        searchQuery = query
        // If content changes and doesn't match selected product, clear selection to show search/list again
        if (selectedProductName != null && query != selectedProductName) {
            selectedProductName = null
        }
    }

    fun clearSelection() {
        selectedProductName = null
        searchQuery = "" // Optional: clear search text too? User usually expects clear on "List Mode" click.
    }

    fun selectProduct(name: String) {
        searchQuery = name
        selectedProductName = name
        
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch potential image using barcode from first entry
            val prices = repo.getProductPrices(name).first()
            val barcode = prices.firstOrNull()?.barcode
            
            if (barcode != null) {
                // Try fetch full info (image)
                // Usamos runCatching para evitar crashes si falla la red (ej. sin internet)
                val info = runCatching { repo.fetchProduct(barcode) }.getOrNull()
                
                // If fetch fails (null), we still have the name.
                // If success, we have image.
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    selectedProductInfo = info ?: ProductInfo(name = name, brand = null)
                }
            } else {
                 kotlinx.coroutines.withContext(Dispatchers.Main) {
                    selectedProductInfo = ProductInfo(name = name, brand = null)
                }
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
