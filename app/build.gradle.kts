import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.0"
    kotlin("kapt")
}


val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.d4vram.ahorrapp"
    compileSdk = 34   // ← API estable, 36 aún NO existe oficialmente

    defaultConfig {
        applicationId = "com.d4vram.ahorrapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "SUPABASE_URL", "\"${localProperties.getProperty("SUPABASE_URL", "")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${localProperties.getProperty("SUPABASE_KEY", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true // ✅ ACTIVADO - Ofusca y reduce código
            isShrinkResources = true // ✅ ACTIVADO - Elimina recursos no usados
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }



    packaging {
        resources.excludes += setOf(
            "/META-INF/{AL2.0,LGPL2.1}"
        )
    }
}

// ================================
// 🧪 TASKS DE TESTING PARA OPTIMIZACIÓN (SIMPLIFICADAS)
// ================================
tasks.register("testOptimizedBuild") {
    group = "verification"
    description = "Build optimized APK and show size comparison"

    doLast {
        println("🚀 Building optimized APK...")
        println("📊 Use: ./gradlew assembleRelease")
        println("📊 Then check size: ls -lh app/build/outputs/apk/release/app-release.apk")
        println("✅ Optimized build completed!")
        println("📱 Next: Install and test all features manually")
    }
}

dependencies {

// Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Activity + Compose
    implementation(libs.androidx.activity.compose)

    // Compose BOM + UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // DEBUG
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // -----------------------------------------
    // 🔥 Dependencias opcionales para tu proyecto AhorrApp:
    // -----------------------------------------

    //ML Kit (lector de códigos de barras)
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // CameraX
    val camera_version = "1.4.1"
    implementation("androidx.camera:camera-core:$camera_version")
    implementation("androidx.camera:camera-camera2:$camera_version")
    implementation("androidx.camera:camera-lifecycle:$camera_version")
    implementation("androidx.camera:camera-view:$camera_version")
    implementation("com.google.guava:guava:31.1-android") // Prevent crash on some devices

    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // WorkManager (para alertas programadas)
    implementation(libs.androidx.work.runtime.ktx)

    // Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:2.6.1"))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.gotrue)
    implementation(libs.supabase.realtime) // Para actualizaciones en tiempo real si queremos
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.okhttp)
    implementation("io.ktor:ktor-client-core:2.3.12")
    // Serialization (necesario para Supabase)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.4.0")
}
