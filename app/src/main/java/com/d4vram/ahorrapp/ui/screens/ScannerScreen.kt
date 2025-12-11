package com.d4vram.ahorrapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import kotlinx.coroutines.delay
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private const val TAG = "ScannerScreen"

@SuppressLint("MissingPermission")
@Composable
fun ScannerScreen(onBarcodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var permissionGranted by remember { mutableStateOf(false) }
    var alreadyHandled by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    // Animaciones
    val scaleAnim = remember { Animatable(1f) }
    val alphaAnim = remember { Animatable(1f) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (!granted) {
            Toast.makeText(context, "Se necesita la cámara para escanear", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) launcher.launch(Manifest.permission.CAMERA)
    }

    if (!permissionGranted) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("Concede acceso a la cámara para empezar a escanear")
            }
        }
        return
    }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                LifecycleCameraController.IMAGE_ANALYSIS or LifecycleCameraController.IMAGE_CAPTURE
            )
        }
    }

    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)
            .build()
        BarcodeScanning.getClient(options)
    }

    val executor = remember { Executors.newSingleThreadExecutor() }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }

    // Animación de éxito
    LaunchedEffect(showSuccessAnimation) {
        if (showSuccessAnimation) {
            // Animación de escala (crece y vuelve)
            scaleAnim.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )

            // Esperar un poco y ocultar
            delay(1500)
            alphaAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300)
            )
            showSuccessAnimation = false
            alphaAnim.snapTo(1f) // Reset para próxima vez
        }
    }

    LaunchedEffect(permissionGranted) {
        if (!permissionGranted) return@LaunchedEffect

        runCatching {
            cameraController.bindToLifecycle(lifecycleOwner)
            cameraController.setImageAnalysisAnalyzer(executor, ImageAnalysis.Analyzer { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage == null) {
                    imageProxy.close()
                    return@Analyzer
                }

                if (alreadyHandled) {
                    imageProxy.close()
                    return@Analyzer
                }

                // Rest of analyzer...
                val input = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

                scanner.process(input)
                    .addOnSuccessListener { barcodes ->
                        val code = barcodes.firstOrNull { it.rawValue != null }?.rawValue?.trim()
                        if (!code.isNullOrEmpty() && !alreadyHandled) {
                            alreadyHandled = true
                            showSuccessAnimation = true

                            // Enviar evento de analytics para escaneo
                            // Nota: El ViewModel manejará el envío del evento completo cuando se guarde el precio
                            onBarcodeScanned(code)
                        }
                    }
                    .addOnFailureListener { e ->
                        // Log.e(TAG, "Error...", e)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            })
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    controller = cameraController
                }
            }
        )

        if (!alreadyHandled) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "Apunta al código de barras",
                        modifier = Modifier
                            .padding(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Animación de éxito 🎉
        if (showSuccessAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scaleAnim.value,
                        scaleY = scaleAnim.value,
                        alpha = alphaAnim.value
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color(0xFF4CAF50), // Verde éxito
                    shape = androidx.compose.foundation.shape.CircleShape,
                    modifier = Modifier.size(120.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Escaneo exitoso",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }
        }
    }
}
