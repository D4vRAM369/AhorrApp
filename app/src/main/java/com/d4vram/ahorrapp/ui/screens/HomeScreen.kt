package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d4vram.ahorrapp.R

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults

@Composable
fun HomeScreen(
    onScan: () -> Unit,
    onHistory: () -> Unit,
    onComparison: () -> Unit,
    onFavorites: () -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(androidx.compose.foundation.rememberScrollState()), // Enable scrolling for small screens
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_ahorrapp_foreground),
                contentDescription = "Logo AhorrApp",
                modifier = Modifier.size(56.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("AhorrApp", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Escanea, comparte y ahorra en Canarias", style = MaterialTheme.typography.bodyMedium)
            }
            // Switch keeps its size
            Switch(
                checked = isDarkMode,
                onCheckedChange = { onToggleTheme() },
                thumbContent = {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Filled.Nightlight else Icons.Filled.WbSunny,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Empezar a escanear", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Escanea el código de barras, añade el precio y ayúdanos a comparar supermercados.", style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onScan, modifier = Modifier.fillMaxWidth()) {
                    Text("Escanear producto")
                }
            }
        }

        Text("Tu actividad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) { // Weight ensures text fills space but respects button
                    Text("Historial de precios", fontWeight = FontWeight.SemiBold)
                    Text("Revisa tus aportaciones", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onHistory) { Text("Ver historial") }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) { // Weight for responsiveness
                    Text("Comparador de precios", fontWeight = FontWeight.SemiBold)
                    Text("Busca y compara ofertas", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onComparison) { 
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Buscar") 
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Productos favoritos", fontWeight = FontWeight.SemiBold)
                    Text("Alertas de precios", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onFavorites,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Favoritos")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp)) // Fixed space instead of weight(1f)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Desarrollado por D4vRAM, con amor por el open source desde Gran Canaria ❤️🇮🇨 \n\n " +
                        "Toda aportación a esta app es bienvenida, por pequeña que sea. Se aceptan también además de método de pago convencional en criptomonedas. Direcciones en la Bio de BuyMeACoffee.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

            // This is the problematic line, you should delete it.
            // androidx.compose.foundation.clickable { uriHandler.openUri("https://www.buymeacoffee.com/D4vRAM369") }

            // This Row already correctly handles the clicks with Buttons.
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { uriHandler.openUri("https://www.buymeacoffee.com/D4vRAM369") },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFFFFDD00))
                ) {
                    Text("☕ Buy me a Coffee", color = androidx.compose.ui.graphics.Color.Black)
                }

                Button(
                    onClick = { uriHandler.openUri("https://github.com/D4vRAM369/") },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Black)
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_github),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("GitHub Profile", color = androidx.compose.ui.graphics.Color.White)
                }
            }

            // Nuevo botón para el repositorio de AhorrApp
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { uriHandler.openUri("https://github.com/D4vRAM369/AhorrApp/") },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF238636) // Verde GitHub
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_github),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    "📱 Ver Repositorio de AhorrApp",
                    color = androidx.compose.ui.graphics.Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.labelLarge
                )
            }

        }
    }
}
