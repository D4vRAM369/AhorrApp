package com.d4vram.ahorrapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d4vram.ahorrapp.R
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import kotlin.math.min
import android.content.Context
import android.widget.Toast



// Función helper para padding responsive
@Composable
fun responsivePadding(): Modifier {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    // Padding horizontal: 16dp para pantallas pequeñas, 24dp para medianas, 32dp para grandes
    val horizontalPadding = when {
        screenWidth < 360 -> 16.dp  // Pantallas muy pequeñas
        screenWidth < 600 -> 20.dp  // Pantallas normales
        else -> 24.dp               // Tablets
    }

    return Modifier.padding(horizontal = horizontalPadding, vertical = 8.dp)
}

// Función helper para tamaño de icono responsive
@Composable
fun responsiveIconSize(): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    return when {
        screenWidth < 360 -> 80.dp   // Pantallas muy pequeñas
        screenWidth < 600 -> 90.dp   // Pantallas normales
        else -> 100.dp               // Tablets
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit, onSkip: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 6 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Skip button en la parte superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onSkip) {
                Text("Saltar", color = MaterialTheme.colorScheme.primary)
            }
        }

        // Pager principal
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> OnboardingPage1()
                1 -> OnboardingPage2()
                2 -> OnboardingPage3()
                3 -> OnboardingPage4()
                4 -> OnboardingPage5()
                5 -> OnboardingPage6(onFinish = onFinish)
            }
        }

        // Indicadores de página y botones de navegación
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Indicadores de página
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                    )
                    if (index < 5) Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de navegación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    ) {
                        Text("Anterior")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp)) // Espaciador vacío
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < 5) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            // Página 5: completar onboarding
                            onFinish()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(if (pagerState.currentPage < 5) "Siguiente" else "¡Comenzar!")
                }
            }
        }
    }
}

@Composable
fun OnboardingPage1() {
    val scrollState = rememberScrollState()

    // Autoscroll suave cuando la página se carga
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500) // Pequeño delay para que se renderice
        scrollState.animateScrollTo(
            value = (scrollState.maxValue * 0.3f).toInt(), // Scroll al 30% del contenido
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 800,
                easing = androidx.compose.animation.core.EaseOutCubic
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(responsivePadding())
    ) {
        // Contenido principal (Header + Texto) - Centrado y con scroll si es necesario
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_ahorrapp_foreground),
                contentDescription = "Logo AhorrApp",
                modifier = Modifier.size(responsiveIconSize())
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "¡Bienvenido a AhorrApp!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Una app creada para combatir la subida masiva y descontrolada de precios de la cesta de la compra en los supermercados de Canarias, en Las Palmas 🇮🇨\n\n" +
                        "La inflación cada vez es un problema más preocupante. Vamos a crear una comunidad donde compartir precios en tiempo real: todos contribuimos, todos ahorramos. \n\n" +
                        "" +
                        "Quizás en el futuro la aplicación esté disponible para más allá de Gran Canaria, pero por el momento empieza como un proyecto local. \n\n Si quieres contribuir, contáctame en Github o en Telegram.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer / Tarjeta
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "💡 ¿Sabías que?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "En Canarias, los precios pueden variar hasta un 30% entre diferentes supermercados del mismo producto.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun OnboardingPage2() {
    val scrollState = rememberScrollState()

    // Autoscroll suave cuando la página se carga
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        scrollState.animateScrollTo(
            value = (scrollState.maxValue * 0.2f).toInt(), // Scroll al 20% del contenido
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 600,
                easing = androidx.compose.animation.core.EaseOutCubic
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(responsivePadding())
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(responsiveIconSize())
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("📱", style = MaterialTheme.typography.displayLarge)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Escanea productos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Usa la cámara de tu teléfono para escanear el código de barras de cualquier producto.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "📋 Pasos:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text("1. Toca 'Escanear producto'", style = MaterialTheme.typography.bodySmall)
                Text("2. Apunta al código de barras", style = MaterialTheme.typography.bodySmall)
                Text("3. Detección automática", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun OnboardingPage3() {
    val scrollState = rememberScrollState()

    // Autoscroll suave cuando la página se carga
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        scrollState.animateScrollTo(
            value = (scrollState.maxValue * 0.25f).toInt(), // Scroll al 25% del contenido
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 700,
                easing = androidx.compose.animation.core.EaseOutCubic
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(responsivePadding())
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(responsiveIconSize())
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("💰", style = MaterialTheme.typography.displayLarge)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Añade precios",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Después de escanear, añade el precio actual y selecciona el supermercado donde lo encontraste. \n\n Ten en cuenta que no todos los productos que escanees estarán disponibles, aunque si la gran mayoría gracias a la API libre de OpenFoodFacts." +
                        " El proceso para añadirlos a nuestra base de datos comunitaria es sencillo: rellena el nombre, marca y completa con más info si es pertinente el campo para detalles adicionales. \n\n Será añadido a la DB automáticamente con el último precio señalado ✅",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "🤝 Tu aportación:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text("• Ayudas a ahorrar a todos", style = MaterialTheme.typography.bodySmall)
                Text("• Creas una base de datos libre", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun OnboardingPage4() {
    val scrollState = rememberScrollState()

    // Autoscroll inteligente: verificar si hay contenido scrolleable después de composición
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000) // Esperar a que se renderice
        if (scrollState.maxValue > 0) { // Si hay contenido para scrollear
            // Scroll inicial
            scrollState.animateScrollTo(
                value = (scrollState.maxValue * 0.2f).toInt(),
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )

            // Scroll progresivo cada 5 segundos
            var currentPosition = 0.2f
            while (currentPosition < 1f) {
                kotlinx.coroutines.delay(5000) // 5 segundos
                currentPosition = min(currentPosition + 0.15f, 1f)
                scrollState.animateScrollTo(
                    value = (scrollState.maxValue * currentPosition).toInt(),
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(responsivePadding())
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(responsiveIconSize())
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("🔗", style = MaterialTheme.typography.displayLarge)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Tecnología mágica",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "AhorrApp combina la API gratuita de OpenFoodFacts con nuestra base de datos comunitaria en Supabase.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🔍 Cómo funciona:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("1. Escaneas código de barras", style = MaterialTheme.typography.bodySmall)
                    Text("2. Consulta automáticamente a la API de OpenFoodFacts", style = MaterialTheme.typography.bodySmall)
                    Text("3. Si no está, rellenas los campos para añadir el producto y el precio para añadirlo a la base de datos", style = MaterialTheme.typography.bodySmall)
                    Text("4. Precios añadidos/actualizados y productos faltantes → Supabase para todos", style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("💡 Tech Stack:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("• OpenFoodFacts: +2M productos", style = MaterialTheme.typography.bodySmall)
                    Text("• Supabase: Realtime DB", style = MaterialTheme.typography.bodySmall)
                    Text("• Kotlin + Compose: App nativa", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun OnboardingPage5() {
    val scrollState = rememberScrollState()

    // Autoscroll suave cuando la página se carga
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        scrollState.animateScrollTo(
            value = (scrollState.maxValue * 0.1f).toInt(), // Scroll mínimo al 10% del contenido
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 400,
                easing = androidx.compose.animation.core.EaseOutCubic
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(responsivePadding())
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .size(responsiveIconSize())
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("🔍", style = MaterialTheme.typography.displayLarge)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Compara y ahorra",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Busca productos por nombre o escanea para ver precios en diferentes supermercados y ahorrar.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "🎯 Beneficios de comparar:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text("• Encuentra el mejor precio disponible", style = MaterialTheme.typography.bodyMedium)
                Text("• Ahorra tiempo en tus compras semanales", style = MaterialTheme.typography.bodyMedium)
                Text("• Toma decisiones informadas", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun OnboardingPage6(onFinish: () -> Unit) {
    val context = LocalContext.current
    var nickname by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(responsivePadding())
    ) {
        // Contenido principal
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono
            Box(
                modifier = Modifier
                    .size(responsiveIconSize())
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", style = MaterialTheme.typography.displayLarge)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Título
            Text(
                "¡Personaliza tu experiencia!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Nickname (Obligatorio)
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Tu nickname *") },
                placeholder = { Text("Ej: CompradorPro") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    androidx.compose.material3.Text("Será visible en tus aportaciones a la comunidad")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Ubicación (Opcional)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Zona de compra (opcional)") },
                placeholder = { Text("Ej: Las Palmas, Tenerife...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    androidx.compose.material3.Text("Nos ayuda a conocer mejor los supermercados de tu zona")
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Texto explicativo
            Text(
                "🌍 Tu nickname y zona nos ayudan a crear una base de datos más rica y precisa. ¡Cada aportación cuenta para mejorar la experiencia de todos!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para guardar y continuar
            Button(
                onClick = {
                    if (nickname.isNotBlank()) {
                        // Guardar nickname y ubicación
                        val userPrefs = context.getSharedPreferences("ahorrapp_prefs", android.content.Context.MODE_PRIVATE)
                        userPrefs.edit()
                            .putString("user_nickname", nickname)
                            .putString("user_location", location.takeIf { it.isNotBlank() })
                            .apply()

                        // Completar onboarding
                        userPrefs.edit().putBoolean("onboarding_completed", true).apply()

                        // Navegar a la siguiente pantalla
                        onFinish()

                        // Navegar a welcome
                        // Nota: La navegación se maneja desde el NavGraph
                    } else {
                        Toast.makeText(context, "Por favor ingresa un nickname", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(24.dp),
                enabled = nickname.isNotBlank()
            ) {
                Text("Siguiente")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta informativa
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "💡 ¿Por qué es útil?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Conocer tu zona nos permite identificar tendencias locales y mejorar las recomendaciones de precios.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}