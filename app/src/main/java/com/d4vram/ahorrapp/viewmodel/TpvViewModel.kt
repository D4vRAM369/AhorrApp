package com.d4vram.ahorrapp.viewmodel


import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
            val productName = productState.product?.name
            runCatching {
                repo.postPrice(barcode, supermarket, price, productName)
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
                    ProductLookupState(
                        isLoading = false,
                        product = product,
                        error = null
                    )
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

data class ProductLookupState(
    val isLoading: Boolean = false,
    val product: ProductInfo? = null,
    val error: String? = null
)

@Composable
fun rememberTpvViewModel(): TpvViewModel {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as Application
    return viewModel(factory = TpvViewModel.provideFactory(app))
}}
