package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d4vram.ahorrapp.R
import com.d4vram.ahorrapp.data.ProductInfo
import com.d4vram.ahorrapp.viewmodel.TpvViewModel
import com.d4vram.ahorrapp.viewmodel.rememberTpvViewModel

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
                val product = productState.product!!
                Column {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        product.brand?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        }
                        if (!product.moreInfo.isNullOrBlank()) {
                            if (product.brand != null) Text(" • ", style = MaterialTheme.typography.bodySmall)
                            Text(product.moreInfo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
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
                colors = OutlinedTextFieldDefaults.colors()
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




        // Fetch existing price when Supermarket or Barcode changes
        LaunchedEffect(selectedSupermarket, customSupermarket, barcode) {
            if (barcode.isNotEmpty()) {
                val market = if (selectedSupermarket == "Otro") customSupermarket.trim() else selectedSupermarket
                if (market.isNotBlank()) {
                    viewModel.fetchExistingPrice(barcode, market)
                }
            }
        }

        // Pre-fill price when existing price is found
        LaunchedEffect(viewModel.existingPrice) {
            viewModel.existingPrice?.let {
                price = it.price.toString()
            }
        }

        Spacer(Modifier.height(20.dp))

        // Comparación con Comunidad (Supabase)
        if (viewModel.isLoadingExistingPrice) {
            Text("Buscando precios en la comunidad...", style = MaterialTheme.typography.bodySmall)
        } else {
            val history = viewModel.historicalPricesForBarcode.sortedByDescending { it.createdAt }
            val userPrice = price.toDoubleOrNull() ?: 0.0
            val currentMarket = if (selectedSupermarket == "Otro") customSupermarket else selectedSupermarket

            if (history.isNotEmpty()) {
                val minPrice = history.minOf { it.price }
                val maxPrice = history.maxOf { it.price }
                val avgPrice = history.map { it.price }.average()

                // Si el usuario está escribiendo un precio, comparamos ESE precio
                val priceToCompare = if (userPrice > 0) userPrice else (viewModel.existingPrice?.price ?: minPrice)
                val diffPercent = ((priceToCompare - avgPrice) / avgPrice * 100)

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (userPrice > 0) "Tu precio vs Comunidad" else "Historial Comunitario",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )

                            // Badge de variación (solo si hay precio para comparar)
                            if (priceToCompare > 0) {
                                Surface(
                                    color = if (diffPercent <= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${if (diffPercent > 0) "+" else ""}${String.format("%.1f", diffPercent)}%",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            if (userPrice > 0) "${priceToCompare}€" else "Comparando...",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(Modifier.height(8.dp))
                        ThermalBar(current = priceToCompare, min = minPrice, max = maxPrice)

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            val minEntry = history.minBy { it.price }
                            val maxEntry = history.maxBy { it.price }
                            Text("mín: ${minEntry.price}€ (${minEntry.supermarket})", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2196F3))
                            Text("máx: ${maxEntry.price}€", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF44336))
                        }
                    }
                }

                // LISTADO DETALLADO (Llenando el "espacio en blanco")
                Spacer(Modifier.height(16.dp))
                Text("Registros en otros establecimientos:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                history.take(5).forEach { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(entry.supermarket, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Text("${entry.price}€", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                entry.createdAt?.let { date ->
                                    val dateStr = try {
                                        val parts = date.split("T")
                                        val day = parts[0].split("-").reversed().joinToString("/")
                                        val time = parts[1].substring(0, 5)
                                        "$day $time"
                                    } catch (e: Exception) { date }
                                    Text(dateStr, style = MaterialTheme.typography.labelSmall)
                                }
                                entry.nickname?.let {
                                    Text("por $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            } else if (currentMarket.isNotBlank() && !viewModel.isLoadingExistingPrice) {
                Text("Sé el primero en registrar este producto en la comunidad 🚀",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

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
            Text(if (viewModel.existingPrice != null) "Actualizar precio" else "Guardar precio")
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

@Composable
fun ThermalBar(current: Double, min: Double, max: Double) {
    val progress = if (max > min) ((current - min) / (max - min)).toFloat() else 0.5f
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF2196F3), // Azul (Barato)
                        Color(0xFFFFC107), // Amarillo (Medio)
                        Color(0xFFF44336)  // Rojo (Caro)
                    )
                )
            )
    ) {
        // Indicador de posición actual
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0.01f, 1f))
                .background(Color.Transparent)
        )
        
        // Marcador (bolita o línea blanca)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(progress.coerceIn(0f, 1f))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(6.dp, 12.dp)
                    .background(Color.White, RoundedCornerShape(2.dp))
                    .border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
            )
        }
    }
}
