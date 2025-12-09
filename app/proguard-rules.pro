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
-keep,allowobfuscation,allowshrinking class kotlinx.serialization.* { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
}

# --- SUPABASE & KTOR ---
-keep class io.ktor.** { *; }
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.ktor.**
-dontwarn io.github.jan.supabase.**

# ===============================
# 📱 ML KIT (BARCODE SCANNING) - CRÍTICO
# ===============================
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**

# ===============================
# 📷 CAMERAX - CRÍTICO
# ===============================
-keep class androidx.camera.** { *; }
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
-dontwarn androidx.room.**

# ===============================
# ⚙️ WORKMANAGER (ALERTAS)
# ===============================
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# ===============================
# 🖼️ COIL (IMAGE LOADING)
# ===============================
-keep class coil.** { *; }
-dontwarn coil.**
-dontwarn org.jetbrains.skiko.**

# ===============================
# 🎨 JETPACK COMPOSE
# ===============================
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**
-dontwarn androidx.compose.runtime.**

# ===============================
# 🌐 RETROFIT & OKHTTP
# ===============================
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# ===============================
# 📱 TU APP ESPECÍFICA
# ===============================
-keep class com.d4vram.ahorrapp.** { *; }
-keep class com.d4vram.ahorrapp.data.** { *; }
-keep class com.d4vram.ahorrapp.viewmodel.** { *; }
-keep class com.d4vram.ahorrapp.ui.screens.** { *; }
-keep class com.d4vram.ahorrapp.workers.** { *; }

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
# ⚠️ DEBUGGING (quitar en producción final)
# ===============================
-keepattributes SourceFile,LineNumberTable # Para crash reporting mejor