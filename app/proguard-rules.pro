# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ===============================
# 🔥 REGLAS CRÍTICAS PARA TU APP
# ===============================

# --- KOTLINX SERIALIZATION (SUPABASE) ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,allowobfuscation,allowshrinking class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
    @kotlinx.serialization.SerialName <fields>;
}

# --- SUPABASE & KTOR ---
-keep class io.ktor.** { *; }
-keep class io.github.jan.supabase.** { *; }
-keep class org.jetbrains.kotlinx.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.coroutines.flow.** { *; }
-dontwarn io.ktor.**
-dontwarn io.github.jan.supabase.**
-dontwarn org.jetbrains.kotlinx.**
-dontwarn kotlinx.coroutines.**
-dontwarn kotlinx.coroutines.flow.**

# --- SLF4J (Ktor logging dependency) ---
-dontwarn org.slf4j.**
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontnote org.slf4j.**

# --- GSON (Retrofit) ---
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# ===============================
# 📱 ML KIT (BARCODE SCANNING) - CRÍTICO - REGLAS SIMPLIFICADAS
# ===============================
# Mantener TODAS las clases de ML Kit (regla más conservadora)
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }

# Reglas específicas mínimas para funcionamiento
-keep class com.google.mlkit.vision.barcode.** { *; }
-keep class com.google.mlkit.vision.barcode.BarcodeScannerOptions { *; }
-keep class com.google.mlkit.vision.barcode.BarcodeScanning { *; }
-keep class com.google.mlkit.vision.common.InputImage { *; }

-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**

# ===============================
# 📷 CAMERAX - CRÍTICO - REGLAS SIMPLIFICADAS
# ===============================
-keep class androidx.camera.** { *; }
-keep class androidx.camera.core.** { *; }
-keep class androidx.camera.lifecycle.** { *; }
-keep class androidx.camera.view.** { *; }
-keep class androidx.lifecycle.** { *; }

-dontwarn androidx.camera.**
-dontwarn androidx.lifecycle.**

# ===============================
# 🗄️ ROOM DATABASE
# ===============================
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Entity class * { *; }
-keep class * extends androidx.room.Dao { *; }
-keep class * extends androidx.room.Database { *; }
-keep class * extends androidx.room.RoomOpenHelper { *; }
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# ===============================
# ⚙️ WORKMANAGER (ALERTAS)
# ===============================
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class androidx.work.** { *; }
-keep class androidx.work.impl.** { *; }
-dontwarn androidx.work.**

# ===============================
# 🖼️ COIL (IMAGE LOADING)
# ===============================
-keep class coil.** { *; }
-keep class coil.request.** { *; }
-keep class coil.decode.** { *; }
-dontwarn coil.**
-dontwarn org.jetbrains.skiko.**

# ===============================
# 🎨 JETPACK COMPOSE - REGLAS EXPANDIDAS
# ===============================
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.animation.** { *; }
-keep class androidx.compose.animation.core.** { *; }

# Mantener clases específicas de animación que usamos
-keep class androidx.compose.animation.core.Animatable { *; }
-keep class androidx.compose.animation.core.AnimationVector1D { *; }
-keep class androidx.compose.ui.graphics.graphicsLayer { *; }

# Mantener Icon y sus recursos
-keep class androidx.compose.material.icons.Icons { *; }
-keep class androidx.compose.material.icons.filled.CheckCircle { *; }

-dontwarn androidx.compose.**
-dontwarn androidx.compose.runtime.**
-dontwarn androidx.compose.ui.**
-dontwarn androidx.compose.material3.**
-dontwarn androidx.compose.foundation.**
-dontwarn androidx.compose.animation.**

# ===============================
# 🌐 RETROFIT & OKHTTP
# ===============================
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep class com.squareup.okhttp3.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**

# ===============================
# 📱 ANDROIDX CORE
# ===============================
-keep class androidx.core.** { *; }
-keep class androidx.activity.** { *; }
-keep class androidx.fragment.** { *; }
-keep class androidx.appcompat.** { *; }
-dontwarn androidx.core.**
-dontwarn androidx.activity.**
-dontwarn androidx.fragment.**
-dontwarn androidx.appcompat.**

# ===============================
# 📱 TU APP ESPECÍFICA - REGLAS EXPANDIDAS
# ===============================
-keep class com.d4vram.ahorrapp.** { *; }
-keep class com.d4vram.ahorrapp.data.** { *; }
-keep class com.d4vram.ahorrapp.viewmodel.** { *; }
-keep class com.d4vram.ahorrapp.ui.screens.** { *; }
-keep class com.d4vram.ahorrapp.workers.** { *; }
-keep class com.d4vram.ahorrapp.navigation.** { *; }

# Mantener funciones específicas que usamos
-keepclassmembers class com.d4vram.ahorrapp.ui.screens.ScannerScreenKt {
    *** ScannerScreen(...);
}

# Mantener funciones de extensión
-keepclassmembers class com.d4vram.ahorrapp.data.RepositoryKt {
    *** getDeviceId(...);
}

# Mantener BuildConfig
-keep class com.d4vram.ahorrapp.BuildConfig { *; }

# Mantener MainActivity y TpvApp
-keep class com.d4vram.ahorrapp.MainActivity { *; }
-keep class com.d4vram.ahorrapp.TpvApp { *; }

# Mantener data classes serializables
-keep class com.d4vram.ahorrapp.data.** {
    *;
}

# Mantener ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# ===============================
# 🛠️ ANDROID GENERAL
# ===============================
-keepattributes SourceFile,LineNumberTable,EnclosingMethod,Signature,Exceptions,InnerClasses,Annotation,RuntimeVisibleAnnotations
-keepattributes *Annotation*
-keepattributes JavascriptInterface

# Keep data for enumerated types
-keepclassmembers enum * { *; }

# Keep native method names
-keepclassmembers class * {
    native <methods>;
}

# Keep setter and getter methods
-keepclassmembers class * {
    void set*(***);
    *** get*();
}

# ===============================
# 🔧 CONFIGURACIÓN OPTIMIZACIÓN
# ===============================
# Optimizaciones agresivas pero seguras
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses

# ===============================
# ⚠️ DEBUGGING (quitar en producción final)
# ===============================
-keepattributes SourceFile,LineNumberTable # Para crash reporting mejor

# ===============================
# 📊 REPORTING (útil durante desarrollo)
# ===============================
-printconfiguration "build/outputs/mapping/configuration.txt"
-printusage "build/outputs/mapping/usage.txt"
-printseeds "build/outputs/mapping/seeds.txt"