package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d4vram.ahorrapp.data.ProductInfo
import com.d4vram.ahorrapp.viewmodel.TpvViewModel
import com.d4vram.ahorrapp.viewmodel.rememberTpvViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.d4vram.ahorrapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceEntryScreen(
    barcode: String,
    onDone: () -> Unit,
    viewModel: TpvViewModel = rememberTpvViewModel()
) {
    var price by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val supermarkets = listOf("Mercadona", "HiperDino", "SPAR", "Carrefour", "ALDI", "Lidl", "Otro")
    var selectedSupermarket by remember { mutableStateOf(supermarkets.first()) }
    var customSupermarket by remember { mutableStateOf("") }

    var showAddProductDialog by remember { mutableStateOf(false) }
    var showNotFoundAlert by remember { mutableStateOf(false) }
    var manualName by remember { mutableStateOf("") }
    var manualBrand by remember { mutableStateOf("") }
    var manualExtra by remember { mutableStateOf("") }
    var fetchCompleted by remember { mutableStateOf(false) }

    val productState = viewModel.productState

    LaunchedEffect(barcode) {
        if (barcode.isNotEmpty()) {
            viewModel.fetchProduct(barcode)
        }
    }

    // Lanza alert y modal cuando no hay producto tras la consulta
    LaunchedEffect(productState.isLoading, productState.product, productState.error) {
        if (productState.isLoading) return@LaunchedEffect
        fetchCompleted = true
        if (productState.product == null && productState.error != null) {
            showNotFoundAlert = true
        } else {
            showNotFoundAlert = false
            showAddProductDialog = false
        }
    }

    Column(Modifier.padding(24.dp)) {

        Text("Código escaneado: $barcode")

        Spacer(Modifier.height(20.dp))

        when {
            productState.isLoading -> {
                Text(
                    "Buscando producto...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            productState.product != null -> {
                val product = productState.product
                Text(
                    text = product.name + (product.brand?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            productState.error != null -> {
                Text(
                    text = "No se pudo obtener el producto. Puedes continuar manualmente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                Text(
                    text = "Producto no encontrado en la base. Introduce los datos manualmente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Precio (€)") },
            isError = priceError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (priceError != null) {
            Text(
                text = priceError ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(20.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (selectedSupermarket == "Otro") customSupermarket else selectedSupermarket,
                onValueChange = {
                    // Solo se edita cuando es "Otro"
                    if (selectedSupermarket == "Otro") customSupermarket = it
                },
                readOnly = selectedSupermarket != "Otro",
                label = { Text("Supermercado") },
                placeholder = { if (selectedSupermarket == "Otro") Text("Introduce el nombre del supermercado") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                supermarkets.forEach { market ->
                    DropdownMenuItem(
                        text = { Text(market) },
                        onClick = {
                            selectedSupermarket = market
                            if (market != "Otro") {
                                customSupermarket = ""
                            }
                            expanded = false
                        }
                    )
                }
            }
        }



        Spacer(Modifier.height(40.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val value = price.toDoubleOrNull()
                if (value == null || value <= 0.0) {
                    priceError = "Introduce un precio válido mayor que 0"
                    return@Button
                }
                if (selectedSupermarket == "Otro" && customSupermarket.isBlank()) {
                    priceError = "Introduce el nombre del supermercado"
                    return@Button
                }
                priceError = null
                val finalSupermarket = if (selectedSupermarket == "Otro") customSupermarket.trim() else selectedSupermarket
                viewModel.sendPrice(barcode, finalSupermarket, value)
                onDone()
            }
        ) {
            Text("Guardar precio")
        }

        Spacer(Modifier.height(12.dp))
        androidx.compose.material3.OutlinedButton(
            onClick = {
                val current = productState.product
                manualName = current?.name ?: ""
                manualBrand = current?.brand ?: ""
                manualExtra = current?.moreInfo ?: ""
                showAddProductDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Añadir más información")
        }
    }

    if (showNotFoundAlert) {
        AlertDialog(
            onDismissRequest = { showNotFoundAlert = false; showAddProductDialog = true },
            confirmButton = {
                Button(onClick = { showNotFoundAlert = false; showAddProductDialog = true }) {
                    Text("Añadir ahora")
                }
            },
            dismissButton = {
                Button(onClick = { showNotFoundAlert = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Producto no encontrado", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
            text = {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.error404),
                        contentDescription = "Producto no encontrado",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Este producto no se ha encontrado. Añádelo y colabora con la comunidad.")
                }
            },
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    }

    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            confirmButton = {
                Button(onClick = {
                    if (manualName.isNotBlank()) {
                        viewModel.overrideProduct(
                            ProductInfo(
                                name = manualName.trim(),
                                brand = manualBrand.takeIf { it.isNotBlank() }?.trim(),
                                moreInfo = manualExtra.takeIf { it.isNotBlank() }?.trim()
                            )
                        )
                        showAddProductDialog = false
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { showAddProductDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Información del producto", color = MaterialTheme.colorScheme.primary) },
            text = {
                Column {
                    Text("Por favor, añade el nombre y la marca para sumarlo a la base de datos.")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = manualName,
                        onValueChange = { manualName = it },
                        label = { Text("Nombre del producto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = manualBrand,
                        onValueChange = { manualBrand = it },
                        label = { Text("Marca (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = manualExtra,
                        onValueChange = { manualExtra = it },
                        label = { Text("Más información") },
                        placeholder = { Text("ej: botella 1.5L, caja 500gr, pack x6...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}
