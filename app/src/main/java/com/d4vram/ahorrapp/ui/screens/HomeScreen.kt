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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d4vram.ahorrapp.R

@Composable
fun HomeScreen(
    onScan: () -> Unit,
    onHistory: () -> Unit,
    onMyProducts: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
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
            Column {
                Text("AhorrApp", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Escanea, comparte y ahorra en Canarias", style = MaterialTheme.typography.bodyMedium)
            }
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Mis productos escaneados", fontWeight = FontWeight.SemiBold)
                    Text("Revisa lo que has aportado", style = MaterialTheme.typography.bodySmall)
                }
                Button(onClick = onMyProducts) { Text("Ver") }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Historial de precios", fontWeight = FontWeight.SemiBold)
                    Text("Últimos precios guardados", style = MaterialTheme.typography.bodySmall)
                }
                Button(onClick = onHistory) { Text("Abrir") }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Gracias a la colaboración de todos podemos comparar precios y ahorrar. Cada aporte suma.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
