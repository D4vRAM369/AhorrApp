# 🧪 Guía de Testing para Build Optimizado

## Paso 1: Generar APK Optimizado
```bash
./gradlew assembleRelease
```

## Paso 2: Verificar Tamaño
```bash
ls -lh app/build/outputs/apk/release/app-release.apk
```

## Paso 3: Instalar y Probar (IMPORTANTE)
```bash
# Instalar APK optimizado
adb install -r app/build/outputs/apk/release/app-release.apk

# Probar TODAS las funcionalidades críticas:
# ✅ Escaneo de códigos de barras
# ✅ Comparación de precios
# ✅ Sincronización con Supabase
# ✅ Alertas de precios (WorkManager)
# ✅ Base de datos local (Room)
# ✅ Modo oscuro
# ✅ Navegación entre pantallas
```

## Paso 4: Verificar Logs
```bash
# Ver logs mientras usas la app
adb logcat | grep -E "(AhorrApp|MLKit|Supabase|WorkManager)"
```

## Paso 5: Si algo falla
```bash
# Desactivar temporalmente para debugging
# En build.gradle.kts:
isMinifyEnabled = false
isShrinkResources = false

# O añadir reglas específicas en proguard-rules.pro
-keep class com.d4vram.ahorrapp.ui.screens.ScannerScreen { *; }
```

## ⚠️ Checklist de Testing
- [ ] App se instala correctamente
- [ ] Escáner de códigos funciona
- [ ] Productos se guardan/cargan
- [ ] Alertas se programan
- [ ] Notificaciones llegan
- [ ] Modo oscuro funciona
- [ ] No hay crashes inesperados