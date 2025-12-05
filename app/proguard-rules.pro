# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# --- KOTLINX SERIALIZATION ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,allowobfuscation,allowshrinking class kotlinx.serialization.* { *; }

# Prevent R8 from stripping Serializer classes for your data classes
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
}

# --- SUPABASE & KTOR ---
-keep class io.ktor.** { *; }
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.ktor.**
-dontwarn io.github.jan.supabase.**

# --- ENTITIES ---
# Keep your data classes from being renamed so Serialization can find fields by name if needed
-keep class com.d4vram.ahorrapp.data.** { *; }

# --- ANDROID COMPONENTS ---
-keepattributes SourceFile,LineNumberTable # Useful for crash reporting

# --- SLF4J (Ktor logging dependency) ---
-dontwarn org.slf4j.**
-dontnote org.slf4j.**

# --- RETROFIT & GSON rules removed by user request (revert) ---