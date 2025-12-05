package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
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
    var supermarket by remember { mutableStateOf("Mercadona") }
    var priceError by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val productState = viewModel.productState

    LaunchedEffect(barcode) {
        if (barcode.isNotEmpty()) {
            viewModel.fetchProduct(barcode)
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

        val supermarkets = listOf("Mercadona", "HiperDino", "SPAR", "Carrefour", "ALDI", "Lidl", "Otro")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = supermarket,
                onValueChange = { supermarket = it },
                label = { Text("Supermercado") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                supermarkets.forEach { market ->
                    DropdownMenuItem(
                        text = { Text(market) },
                        onClick = {
                            supermarket = market
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        Button(modifier = Modifier.fillMaxWidth(),
            onClick = {
                val value = price.toDoubleOrNull()
                if (value == null || value <= 0.0) {
                    priceError = "Introduce un precio válido mayor que 0"
                    return@Button
                }
                priceError = null
                viewModel.sendPrice(barcode, supermarket, value)
                onDone()
            }) {
            Text("Guardar precio")
        }
    }
}
