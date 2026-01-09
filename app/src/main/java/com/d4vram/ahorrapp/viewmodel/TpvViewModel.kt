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
import com.d4vram.ahorrapp.data.PushMessage
import com.d4vram.ahorrapp.data.UserFavorite
import com.d4vram.ahorrapp.data.PriceAlert
import com.d4vram.ahorrapp.data.SupabasePriceEntry
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
import kotlinx.coroutines.withContext
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import com.d4vram.ahorrapp.workers.PriceAlertWorker
import java.util.concurrent.TimeUnit

class TpvViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository(application.applicationContext)

    // Store Device ID - Moved up to avoid NPE in init
    private val deviceId: String by lazy {
        android.provider.Settings.Secure.getString(application.contentResolver, android.provider.Settings.Secure.ANDROID_ID) ?: "unknown_device"
    }

    var productState by mutableStateOf(ProductLookupState())
        private set

    var existingPrice by mutableStateOf<SupabasePriceEntry?>(null)
        private set

    var historicalPricesForBarcode by mutableStateOf<List<SupabasePriceEntry>>(emptyList())
        private set

    var isLoadingExistingPrice by mutableStateOf(false)
        private set

    var fetchError by mutableStateOf<String?>(null)
        private set

    fun fetchExistingPrice(barcode: String, supermarket: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val cleanBarcode = barcode.trim()
            val cleanMarket = supermarket.trim()
            isLoadingExistingPrice = true
            existingPrice = null
            historicalPricesForBarcode = emptyList()
            fetchError = null
            
            // Lanzamos ambas peticiones
            val latestResult = repo.getLatestPriceForBarcode(cleanBarcode, cleanMarket)
            val allResult = repo.getAllPricesForBarcode(cleanBarcode)
            
            latestResult.onSuccess { entry ->
                existingPrice = entry
            }.onFailure { error ->
                fetchError = error.message
            }

            allResult.onSuccess { list ->
                historicalPricesForBarcode = list
            }
            
            isLoadingExistingPrice = false
        }
    }

    fun observeHistory(): Flow<List<PriceEntryEntity>> = repo.observePriceHistory()

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

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _currentNickname = MutableStateFlow("Usuario Anónimo")
    val currentNickname: StateFlow<String> = _currentNickname.asStateFlow()

    private val _scanSoundEnabled = MutableStateFlow(true)
    val scanSoundEnabled: StateFlow<Boolean> = _scanSoundEnabled.asStateFlow()

    private val _showOnboarding = MutableStateFlow(true)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    private val _currentPushMessage = MutableStateFlow<PushMessage?>(null)
    val currentPushMessage: StateFlow<PushMessage?> = _currentPushMessage.asStateFlow()

    // deviceId is already declared at the top of the class

    init {
        checkLicense()
        loadOnboardingState() // Aquí está el OnboardingState, solo que no sé si la primera al abrir la actualización con las 5 pantallas, o la que se abre cada vez que abrimos la app, tengo que mirarlo, preguntar y apuntar.
        loadUserSettings() // Nueva función para cargar sonidos, etc.
        loadUserFavorites()
        loadUserAlerts()
    }

    private fun loadUserSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val prefs = getApplication<Application>().getSharedPreferences("ahorrapp_prefs", android.content.Context.MODE_PRIVATE)
            _scanSoundEnabled.value = prefs.getBoolean("scan_sound_enabled", true)
        }
    }

    fun toggleScanSound() {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = !_scanSoundEnabled.value
            _scanSoundEnabled.value = newValue
            val prefs = getApplication<Application>().getSharedPreferences("ahorrapp_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putBoolean("scan_sound_enabled", newValue).apply()
        }
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
            // También guardar localmente para respaldo
            val prefs = getApplication<Application>().getSharedPreferences("ahorrapp_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString("user_nickname", newName).apply()
        }
    }

    private fun loadOnboardingState() {
        viewModelScope.launch(Dispatchers.IO) {
            val prefs = getApplication<Application>().getSharedPreferences("ahorrapp_prefs", android.content.Context.MODE_PRIVATE)
            val hasCompletedOnboarding = prefs.getBoolean("onboarding_completed", false)
            _showOnboarding.value = !hasCompletedOnboarding

            // NO verificar mensajes push aquí - se hace en welcome/home
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch(Dispatchers.IO) {
            val prefs = getApplication<Application>().getSharedPreferences("ahorrapp_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putBoolean("onboarding_completed", true).apply()
            _showOnboarding.value = false
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
                    moreInfo = currentProduct?.moreInfo,
                    deviceId = deviceId, // Nuevo para v1.1
                    nickname = _currentNickname.value // Enviamos nickname
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
                moreInfo = null,
                deviceId = deviceId, // Nuevo para v1.1
                nickname = _currentNickname.value
            )
        }
    }
// ...
    fun syncEntry(entry: PriceEntryEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repo.postPrice(
                barcode = entry.barcode,
                supermarket = entry.supermarket,
                price = entry.price,
                productName = entry.productName,
                brand = null, // Entry doesn't store brand yet, maybe later
                moreInfo = null, // Entry doesn't store moreInfo yet
                deviceId = deviceId, // Nuevo para v1.1
                nickname = _currentNickname.value
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
                                        id = 0, // Auto-generate
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

    var selectedProductBarcode by mutableStateOf<String?>(null)
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

    // --- FAVORITOS Y ALERTAS ---
    private val _userFavorites = MutableStateFlow<List<UserFavorite>>(emptyList())
    val userFavorites: StateFlow<List<UserFavorite>> = _userFavorites.asStateFlow()

    private val _userAlerts = MutableStateFlow<List<PriceAlert>>(emptyList())
    val userAlerts: StateFlow<List<PriceAlert>> = _userAlerts.asStateFlow()

    var isLoadingFavorites by mutableStateOf(false)
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
        selectedProductBarcode = null
        searchQuery = "" // Optional: clear search text too? User usually expects clear on "List Mode" click.
    }

    fun selectProduct(name: String) {
        searchQuery = name
        selectedProductName = name

        viewModelScope.launch(Dispatchers.IO) {
            // Fetch potential image using barcode from first entry
            val prices = repo.getProductPrices(name).first()
            val barcode = prices.firstOrNull()?.barcode
            selectedProductBarcode = barcode

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

    fun fetchProduct(barcode: String) {
        viewModelScope.launch {
            productState = productState.copy(isLoading = true, error = null)
            val result = runCatching {
                withContext(Dispatchers.IO) { repo.fetchProduct(barcode) }
            }.getOrNull()

            productState = productState.copy(
                isLoading = false,
                product = result,
                error = if (result == null) "Producto no encontrado" else null
            )
        }
    }

    fun overrideProduct(info: ProductInfo) {
        productState = productState.copy(product = info, error = null)
    }

    // --- FUNCIONES PARA FAVORITOS Y ALERTAS ---

    private fun loadUserFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val favorites = repo.getUserFavorites(deviceId)
            _userFavorites.value = favorites
        }
    }

    private fun loadUserAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            val alerts = repo.getUserAlerts(deviceId)
            _userAlerts.value = alerts
        }
    }

    fun addToFavorites(barcode: String, productName: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoadingFavorites = true
            val success = repo.addToFavorites(deviceId, barcode, productName)
            if (success) {
                loadUserFavorites() // Recargar lista
            }
            isLoadingFavorites = false
        }
    }

    fun removeFromFavorites(barcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoadingFavorites = true
            val success = repo.removeFromFavorites(deviceId, barcode)
            if (success) {
                loadUserFavorites() // Recargar lista
            }
            isLoadingFavorites = false
        }
    }

    fun isProductFavorite(barcode: String): Boolean {
        return _userFavorites.value.any { it.barcode == barcode }
    }

    fun createPriceAlert(barcode: String, targetPrice: Double? = null, alertPercentage: Double = 10.0) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repo.createOrUpdatePriceAlert(deviceId, barcode, targetPrice, alertPercentage)
            if (success) {
                loadUserAlerts() // Recargar alertas
                // Programar WorkManager para verificar alertas
                schedulePriceAlerts()
            }
        }
    }

    fun removePriceAlert(barcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Para "remover" una alerta, la desactivamos
            runCatching {
                repo.createOrUpdatePriceAlert(deviceId, barcode, alertPercentage = 999.0) // Porcentaje imposible
            }
            loadUserAlerts()
        }
    }

    // --- WORKMANAGER PARA ALERTAS ---

    fun schedulePriceAlerts() {
        val workManager = WorkManager.getInstance(getApplication())

        // Crear constraints para ejecutar solo con conectividad
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Crear trabajo periódico (cada 6 horas)
        val priceAlertWork = PeriodicWorkRequestBuilder<PriceAlertWorker>(
            6, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS) // Primera ejecución en 1 hora
            .build()

        // Programar el trabajo
        workManager.enqueueUniquePeriodicWork(
            "price_alert_check",
            ExistingPeriodicWorkPolicy.REPLACE, // Reemplazar si ya existe
            priceAlertWork
        )
    }

    fun cancelPriceAlerts() {
        val workManager = WorkManager.getInstance(getApplication())
        workManager.cancelUniqueWork("price_alert_check")
    }

    // Funciones para sistema de mensajes push
    fun loadPushMessagesForWelcome() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val deviceId = getDeviceIdStr()
                val unseenMessages = repo.getUnseenPushMessages(deviceId)
                // Mostrar solo el mensaje más prioritario
                _currentPushMessage.value = unseenMessages.firstOrNull()
            } catch (e: Exception) {
                android.util.Log.d("TpvViewModel", "Failed to load push messages: ${e.message}")
            }
        }
    }

    fun markMessageViewed(messageId: String, action: String = "viewed") {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceId = getDeviceIdStr()
            repo.markMessageViewed(messageId, deviceId, action)
            _currentPushMessage.value = null
        }
    }

    fun dismissPushMessage() {
        _currentPushMessage.value = null
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
