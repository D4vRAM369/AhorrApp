package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d4vram.ahorrapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import kotlinx.coroutines.flow.filter

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    val scrollState = rememberScrollState()
    var autoScrollEnabled by remember { mutableStateOf(true) }

    // Desactiva auto-scroll al detectar interacción manual
    LaunchedEffect(scrollState.isScrollInProgress) {
        if (scrollState.isScrollInProgress) autoScrollEnabled = false
    }

    // Auto-scroll suave arriba/abajo para darle presencia
    LaunchedEffect(autoScrollEnabled) {
        if (!autoScrollEnabled) return@LaunchedEffect


        // Bucle de auto-scroll mientras no haya interacción
        while (isActive && autoScrollEnabled) {
            val max = scrollState.maxValue
            if (max > 0) {
                scrollState.animateScrollTo(
                    value = max,
                    animationSpec = tween(durationMillis = 4500, easing = LinearEasing)
                )
                delay(700)
                scrollState.animateScrollTo(
                    value = 0,
                    animationSpec = tween(durationMillis = 4500, easing = LinearEasing)
                )
                delay(700)
            } else {
                delay(500)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
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
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Empezar ahora")
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}
// The extra spacer and brace that were here have been removed.


@Composable
fun LockScreen(deviceId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔒", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(24.dp))
        Text(
            "Acceso Restringido",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Esta aplicación ha sido desactivada en este dispositivo. Probablemente hay una actualización pendiente o se ha detectado un error crítico en el que estoy trabajando.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ID del Dispositivo:", style = MaterialTheme.typography.labelMedium)
                Text(deviceId, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Para más información, contáctame en:",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            "t.me/D4vRAM369",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}
