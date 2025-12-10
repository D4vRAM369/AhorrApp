-# AhorrApp 💰📱🛒

[![AhorrApp icon](https://raw.githubusercontent.com/D4vRAM369/ahorrapp/main/app/src/main/res/mipmap-xxxhdpi/ic_ahorrapp.webp)](https://raw.githubusercontent.com/D4vRAM369/ahorrapp/main/app/src/main/res/mipmap-xxxhdpi/ic_ahorrapp.webp)

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) [![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?logo=android)](https://developer.android.com/) [![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-7F52FF?logo=kotlin)](https://kotlinlang.org/) [![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.0-4285F4?logo=android)](https://developer.android.com/jetpack/compose) [![Supabase](https://img.shields.io/badge/Supabase-2.0-3ECF8E?logo=supabase)](https://supabase.com/) [![OpenFoodFacts](https://img.shields.io/badge/OpenFoodFacts-API-00A65A)](https://world.openfoodfacts.org/) [![](https://img.shields.io/badge/Made_with_Love_&_Coffee-ff69b4)](https://img.shields.io/badge/Made_with_Love_&_Coffee-ff69b4) [![](https://img.shields.io/badge/Project_Based_Learning-orange?logo=gradle)](https://en.wikipedia.org/wiki/Project-based_learning) [![](https://img.shields.io/badge/Built_with_Claude_Code-8A2BE2?logo=anthropic)](https://claude.ai/code) [![](https://img.shields.io/badge/ChatGPT-74aa9c?logo=openai&logoColor=white)](https://chat.openai.com/)

[🇬🇧🇺🇸 English version](/D4vRAM369/ahorrapp/blob/main/README_English-version.md)

## Tabla de Contenidos

- [¿Qué es AhorrApp?](#qué-es-ahorrapp)
- [Características Principales](#características-principales)
- [Instalación](#instalación)
- [Cómo Usar](#cómo-usar-ahorrapp)
- [Tecnología](#tecnología)
- [Arquitectura](#arquitectura-del-proyecto)
- [Contribuir](#contribuir)
- [Licencia](#licencia)

## ¿Qué es AhorrApp?

**AhorrApp** es una aplicación Android inteligente y comunitaria diseñada para ayudarte a **ahorrar dinero** mediante la comparación de precios de productos. Desarrollada en **Kotlin** y **Jetpack Compose**, esta herramienta permite escanear códigos de barras, registrar precios en diferentes supermercados y recibir alertas inteligentes cuando bajen los precios de tus productos favoritos.

Ideal para consumidores conscientes que quieren optimizar sus compras y contribuir a una base de datos comunitaria de precios.

## ¿Por qué usar AhorrApp?

### 💰 Ahorra Dinero Real

- **Comparación inteligente** de precios entre supermercados
- **Alertas automáticas** cuando bajan los precios de tus productos favoritos
- **Historial personal** para rastrear tus mejores compras
- **Base de datos comunitaria** con precios reales de usuarios

### 📱 Experiencia Moderna y Fluida

- **Interfaz intuitiva** con Jetpack Compose y Material Design 3
- **Escáner avanzado** de códigos de barras con ML Kit
- **Modo oscuro/claro** automático
- **Onboarding completo** para nuevos usuarios

### 🌐 Comunidad y Colaboración

- **Datos compartidos** de manera anónima y segura
- **Información nutricional** integrada con OpenFoodFacts
- **Sistema de licencias** justo por dispositivo
- **Contribución activa** a la economía colaborativa

## Características Principales

### 🛒 Gestión Inteligente de Precios

- **Escáner de códigos de barras** con ML Kit y CameraX
- **Registro de precios** por supermercado y ubicación
- **Base de datos comunitaria** usando Supabase
- **Historial completo** de todas tus aportaciones

### 🔔 Sistema de Alertas Avanzado

- **Alertas configurables** para productos favoritos
- **Notificaciones push** automáticas con WorkManager
- **Seguimiento inteligente** de cambios de precio
- **Precios objetivo** personalizables

### ⭐ Funcionalidades Premium

- **Productos favoritos** para seguimiento personalizado
- **Comparador inteligente** entre múltiples supermercados
- **Integración con OpenFoodFacts** para datos nutricionales
- **Interfaz moderna** y responsive

### 🔒 Privacidad y Seguridad

- **Datos anónimos** en la base comunitaria
- **Licencias por dispositivo** justas
- **Sin trackers** ni recopilación innecesaria
- **Código abierto** y auditable

## 📸 Capturas de Pantalla

*Próximamente - Capturas de pantalla de la aplicación en funcionamiento*

## Instalación

### Lo que Necesitas

- Android 7.0 (API 24) o superior
- ~50MB de espacio disponible
- Cámara para escanear códigos de barras
- Conexión a internet para sincronización comunitaria

### Cómo Instalar

#### Opción 1: Desde GitHub Releases

1. Ve a la sección [Releases](https://github.com/D4vRAM369/ahorrapp/releases)
2. Descarga el APK más reciente
3. Instala en tu dispositivo Android
4. Concede permisos de cámara cuando se solicite

#### Opción 2: Compilar desde Código Fuente

```bash
git clone https://github.com/D4vRAM369/ahorrapp.git
cd ahorrapp
./gradlew assembleRelease
```

### Configuración Inicial

1. **Primera ejecución**: La app te guiará con un onboarding completo
2. **Licencia**: Registra tu dispositivo para acceder a todas las funciones
3. **Permisos**: Concede acceso a la cámara para escanear productos
4. **Sincronización**: Los datos comunitarios se descargarán automáticamente

## Cómo Usar AhorrApp

### 🚀 Primeros Pasos

1. **Onboarding**: Sigue las instrucciones iniciales
2. **Licencia**: Registra tu dispositivo
3. **Permisos**: Autoriza el uso de la cámara

### 📱 Funcionalidades Principales

1. **Escanear Producto**
   - Abre la pantalla de escáner
   - Apunta la cámara al código de barras
   - La app reconocerá automáticamente el producto

2. **Registrar Precio**
   - Selecciona el supermercado actual
   - Ingresa el precio observado
   - Confirma para compartir con la comunidad

3. **Comparar Precios**
   - Ve todos los precios reportados para ese producto
   - Compara entre diferentes supermercados
   - Identifica las mejores ofertas

4. **Marcar Favoritos**
   - Toca el corazón en cualquier producto
   - Recibe alertas cuando baje el precio
   - Configura precios objetivo personalizados

5. **Ver Historial**
   - Revisa todas tus aportaciones
   - Rastrea tus mejores compras
   - Analiza tus patrones de ahorro

## Tecnología

Este proyecto es un excelente ejemplo de desarrollo Android moderno con enfoque comunitario:

- **Lenguaje**: [Kotlin](https://kotlinlang.org/) 1.9.0+
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) con Material Design 3
- **Arquitectura**: MVVM (Model-View-ViewModel) con Flows
- **Base de Datos**: [Room](https://developer.android.com/training/data-storage/room) para local + [Supabase](https://supabase.com/) para comunitario
- **APIs Externas**: [OpenFoodFacts](https://world.openfoodfacts.org/) para información nutricional
- **ML**: [Google ML Kit](https://developers.google.com/ml-kit) para reconocimiento de códigos de barras
- **Cámara**: [CameraX](https://developer.android.com/training/camerax) para captura avanzada
- **Trabajo en Segundo Plano**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) para alertas
- **Imágenes**: [Coil](https://coil-kt.github.io/coil/) para carga eficiente
- **Red**: [Retrofit](https://square.github.io/retrofit/) + [Gson](https://github.com/google/gson/)
- **Build System**: Gradle Kotlin DSL

## Arquitectura del Proyecto

```
app/src/main/java/com/d4vram/ahorrapp/
├── data/                    # 🗄️ Capa de datos
│   ├── AppDatabase.kt      # Configuración Room local
│   ├── Repository.kt       # Repositorio principal
│   ├── OpenFoodApiService.kt # API de OpenFoodFacts
│   ├── OpenFoodProductResponse.kt # Modelos de respuesta
│   ├── PriceDao.kt         # Acceso a datos de precios
│   ├── PriceEntryEntity.kt # Entidades Room
│   ├── PricePayload.kt     # Modelos de API
│   └── ProductInfo.kt      # Información de productos
├── ui/                     # 🎨 Capa de presentación
│   ├── screens/            # Pantallas principales
│   │   ├── ComparisonScreen.kt
│   │   ├── FavoritesScreen.kt
│   │   ├── HistoryScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── OnboardingScreen.kt
│   │   ├── PriceEntryScreen.kt
│   │   ├── ProfileScreen.kt
│   │   ├── ScannerScreen.kt
│   │   └── WelcomeScreen.kt
│   └── theme/              # Tema y estilos
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── viewmodel/              # 🧠 Lógica de negocio
│   ├── RememberTpvViewModel.kt
│   └── TpvViewModel.kt
├── navigation/             # 🧭 Navegación
│   └── NavGraph.kt
├── workers/                # ⚙️ Tareas en segundo plano
│   └── PriceAlertWorker.kt
└── MainActivity.kt         # 📱 Actividad principal
```

## 📊 Datos Técnicos

### Versiones Soportadas
- **Android Target**: API 34 (Android 14)
- **Android Mínimo**: API 24 (Android 7.0)
- **Kotlin**: 1.9.0
- **Jetpack Compose**: 1.5.x

### Base de Datos Comunitaria (Supabase)

**Tabla: prices**
```sql
CREATE TABLE prices (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  barcode TEXT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  supermarket TEXT NOT NULL,
  user_id TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  product_name TEXT,
  brand TEXT
);
```

**Tabla: favorites**
```sql
CREATE TABLE favorites (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  user_id TEXT NOT NULL,
  barcode TEXT NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

**Tabla: price_alerts**
```sql
CREATE TABLE price_alerts (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  user_id TEXT NOT NULL,
  barcode TEXT NOT NULL,
  target_price DECIMAL(10,2) NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

## 🤝 Contribuir

¿Quieres mejorar AhorrApp? Las contribuciones son bienvenidas:

1. Fork del repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Sigue las convenciones de Kotlin/Android
4. Envía tu PR con descripción detallada

### Guías de Contribución
- Consulta [CONTRIBUTING.md](CONTRIBUTING.md) para detalles
- Sigue el estilo de código establecido
- Añade tests para nuevas funcionalidades
- Documenta cambios significativos

## 📄 Licencia

Este proyecto está bajo la **Licencia GPL-3.0** - ver el archivo [LICENSE](LICENSE) para más detalles.

## 🙏 Agradecimientos

- **[OpenFoodFacts](https://world.openfoodfacts.org/)** por la base de datos nutricional
- **[Supabase](https://supabase.com/)** por el backend comunitario
- **[Google ML Kit](https://developers.google.com/ml-kit)** por el reconocimiento de códigos
- **Comunidad Android** por las librerías y herramientas
- **Contribuidores** que hacen posible este proyecto

## 💬 Soporte al Proyecto

Si AhorrApp te ayuda a ahorrar dinero y tomar mejores decisiones de compra, considera apoyar el proyecto:

[![GitHub stars](https://img.shields.io/github/stars/D4vRAM369/ahorrapp?style=social)](https://github.com/D4vRAM369/ahorrapp/stargazers) [![Buy me a coffee](https://img.shields.io/badge/Buy_me_a_coffee-FFDD00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black)](https://www.buymeacoffee.com/D4vRAM369)

---

*Desarrollado con ❤️ y ☕ para la comunidad de consumidores inteligentes.*

## About

Aplicación Android inteligente para ahorrar dinero mediante comparación de precios comunitaria. Escanea productos, registra precios y recibe alertas cuando bajen los precios de tus productos favoritos.