package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun WelcomeScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.mipmap.ic_ahorrapp_foreground),
            contentDescription = "Logo AhorrApp",
            modifier = Modifier
                .height(120.dp)
        )
        Text("Bienvenido a AhorrApp", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Escanea códigos de barras, añade los precios que faltan a la base de datos, y colaboremos para comparar y encontrar los mejores precios de los productos que compramos día a día, en los diferentes supermercados en Canarias. \n\nEstamos hartos y cansados de tanta subida de precio, y esta aplicación surge para combatirlo. Entre todos podemos \uD83D\uDC65\uD83C\uDDEE\uD83C\uDDE8\uD83D\uDCAA " ,
            style = MaterialTheme.typography.bodyMedium
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("¿Cómo funciona?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("1. Escanea el producto con la cámara.", style = MaterialTheme.typography.bodyMedium)
                Text("2. Añade el precio y selecciona el supermercado.", style = MaterialTheme.typography.bodyMedium)
                Text("3. Enviamos y guardamos la info para que todos puedan comparar.", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Beneficios de colaborar", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("• Encuentras el mejor lugar para comprar cada producto al mejor precio.", style = MaterialTheme.typography.bodyMedium)
                Text("• Ahorras tiempo comparando, a la vez que revindicas en contra de las subidas de precio constantes.", style = MaterialTheme.typography.bodyMedium)
                Text("• Te conviertes en parte de una comunidad que se informa entre sí.", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
            Text("Empezar ahora")
        }
    }
}
