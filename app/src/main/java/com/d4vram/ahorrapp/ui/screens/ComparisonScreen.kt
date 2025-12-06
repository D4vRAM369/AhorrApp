package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.d4vram.ahorrapp.viewmodel.TpvViewModel
import com.d4vram.ahorrapp.data.PriceEntryEntity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    viewModel: TpvViewModel,
    onBack: () -> Unit
) {
    // Collect state
    val searchQuery = viewModel.searchQuery
    val searchResults by viewModel.searchResults.collectAsState()
    val comparisonPrices by viewModel.comparisonPrices.collectAsState()
    val selectedProductInfo = viewModel.selectedProductInfo
    val selectedName = viewModel.selectedProductName
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Header / Search ---
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    label = { Text("Buscar producto...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
            }

            // --- Product Details & Comparison ---
            if (selectedName != null) {
                
                // Product Info Header
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!selectedProductInfo?.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = selectedProductInfo?.imageUrl,
                            contentDescription = selectedName,
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color.White, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // Placeholder
                        Box(
                            modifier = Modifier
                               .size(80.dp)
                               .background(Color.LightGray, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = selectedName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        selectedProductInfo?.brand?.let { 
                            Text(text = it, style = MaterialTheme.typography.bodyMedium, color = Color.Gray) 
                        }
                    }
                }

                Text("Comparativa de precios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                // Prices List (Latest Updates)
                // Usamos weight(1f) para que ocupe el espacio RESTANTE y no se salga de pantalla
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFCDDC39)), // Lime
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) 
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Últimas actualizaciones", 
                            style = MaterialTheme.typography.labelLarge, 
                            color = Color.Black, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        if (comparisonPrices.isEmpty()) {
                            Text("No hay precios registrados aún.", color = Color.Black)
                        } else {
                            val minPrice = comparisonPrices.minOfOrNull { it.price } ?: 0.0

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(comparisonPrices) { entry ->
                                    val isBestPrice = (entry.price == minPrice) && (minPrice > 0.0)
                                    PriceComparisonCard(entry, isBestPrice, minPrice)
                                }
                            }
                        }
                    }
                }
            } else {
                 // Empty State message while typing or initial
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                     Text("Busca un producto para comparar precios", color = Color.Gray, modifier = Modifier.padding(top = 32.dp))
                 }
            }
        }

        // --- Suggestions Overlay ---
        // Z-Index superior natural al estar al final del Box
        if (searchResults.isNotEmpty() && (selectedName == null || selectedName != searchQuery)) {
            // Posicionamos justo debajo de la barra de búsqueda (approx padding top 80dp)
            // Una solución robusta es usar Popup o simplemente padding relativo
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 85.dp, start = 16.dp, end = 16.dp) // Ajuste manual para coincidir con el TextField
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp), // Limit height
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    LazyColumn {
                        items(searchResults) { product ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        viewModel.selectProduct(product)
                                        focusManager.clearFocus() // Hide keyboard
                                    }
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = product, style = MaterialTheme.typography.bodyLarge)
                            }
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriceComparisonCard(entry: PriceEntryEntity, isBestPrice: Boolean, minPrice: Double) {
    val priceDiffPercent = if (minPrice > 0) ((entry.price - minPrice) / minPrice) * 100 else 0.0
    
    val backgroundColor = when {
        isBestPrice -> Color(0xFFFFEB3B) // Amarillo (Mejor precio)
        priceDiffPercent < 10 -> Color(0xFFE3F2FD) // Azul muy claro
        priceDiffPercent < 20 -> Color(0xFFBBDEFB) // Azul claro
        priceDiffPercent < 30 -> Color(0xFF64B5F6) // Azul intenso
        priceDiffPercent < 50 -> Color(0xFFFFCC80) // Naranja claro
        else -> Color(0xFFEF9A9A) // Rojo claro (Muy caro)
    }

    val textColor = Color.Black // Generalmente negro va bien con estos pasteles, pero se puede ajustar

    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(entry.supermarket, fontWeight = FontWeight.Bold, color = textColor)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Text(dateFormat.format(Date(entry.timestamp)), style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%.2f €".format(entry.price), 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isBestPrice) FontWeight.ExtraBold else FontWeight.Normal,
                    color = textColor
                )
                if (isBestPrice) {
                    Text(
                        "¡Mejor precio!", 
                        color = Color(0xFFF57F17), 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Bold
                    )
                } else {
                     Text(
                        "+%.0f%%".format(priceDiffPercent), 
                        color = Color.DarkGray, 
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
