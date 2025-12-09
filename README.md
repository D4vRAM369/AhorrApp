# AhorrApp 📱🛒

**Escanea, compara y ahorra en tus compras diarias en Canarias** 🇮🇨

[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2024+-green.svg)](https://developer.android.com/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.0-blue.svg)](https://developer.android.com/jetpack/compose)
![Build](https://github.com/D4vRAM369/AhorrApp/workflows/CI/badge.svg)
![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)

AhorrApp es una aplicación móvil gratuita y de código abierto diseñada para ayudar a los consumidores canarios a tomar decisiones de compra más inteligentes. Escanea códigos de barras, registra precios en diferentes supermercados y accede a una base de datos comunitaria para comparar ofertas y encontrar los mejores precios.

## 🚀 ¿Por qué AhorrApp?

En Canarias, los precios pueden variar significativamente entre supermercados, y no siempre es fácil saber dónde encontrar la mejor oferta. AhorrApp resuelve este problema permitiendo a los usuarios:

- **Escanear productos** con la cámara del teléfono
- **Registrar precios** en tiempo real
- **Comparar ofertas** entre supermercados
- **Recibir alertas** cuando bajan los precios de productos favoritos
- **Contribuir a la comunidad** compartiendo información de precios

Todo de forma gratuita, sin anuncios y respetando tu privacidad.

## ✨ Características principales

### 📱 Funcionalidades core
- **Escáner de códigos de barras** integrado con ML Kit
- **Registro de precios** por supermercado y producto
- **Base de datos comunitaria** alimentada por usuarios
- **Historial personal** de precios registrados
- **Comparador inteligente** de ofertas

### 🔔 Sistema de alertas
- **Productos favoritos** para seguimiento
- **Alertas de precio** configurables (porcentaje o precio objetivo)
- **Notificaciones automáticas** cuando bajan los precios

### 🎨 Experiencia de usuario
- **Interfaz moderna** con Jetpack Compose
- **Modo oscuro/claro** automático
- **Diseño adaptativo** para diferentes tamaños de pantalla
- **Navegación intuitiva** y fluida

### 🔒 Privacidad y seguridad
- **Sin recopilación de datos personales** (solo precios y códigos de barras)
- **Licencia por dispositivo** para control de calidad
- **Código 100% open source** y auditable

## 📸 Capturas de pantalla

### Pantalla principal
<img src="screenshots/home_screen.png" width="300" alt="Pantalla principal de AhorrApp">

### Escáner de códigos de barras
<img src="screenshots/scanner_screen.png" width="300" alt="Escáner de códigos de barras">

### Comparador de precios
<img src="screenshots/comparison_screen.png" width="300" alt="Comparador de precios">

### Historial de precios
<img src="screenshots/history_screen.png" width="300" alt="Historial de precios">

> **Nota**: Las capturas de pantalla deben añadirse en la carpeta `screenshots/` del repositorio.

## 🛠️ Tecnologías utilizadas

- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose
- **Backend**: Supabase (PostgreSQL + Auth)
- **APIs externas**: OpenFoodFacts para información de productos
- **Base de datos local**: Room
- **Escáner**: ML Kit (Google)
- **Cámara**: CameraX
- **Trabajos en segundo plano**: WorkManager

## 📋 Requisitos

- **Android**: API 24+ (Android 7.0)
- **Permisos**: Cámara (para escanear códigos de barras)

## 🚀 Instalación y configuración

### Prerrequisitos
- **Android Studio**: Versión Arctic Fox o superior
- **JDK**: Versión 11
- **Cuenta Supabase**: Para la base de datos backend

### 1. Clonar el repositorio
```bash
git clone https://github.com/D4vRAM369/AhorrApp.git
cd AhorrApp
```

### 2. Configurar API keys
Crea un archivo `local.properties` en la raíz del proyecto con tus credenciales de Supabase:

```properties
SUPABASE_URL=https://tu-proyecto.supabase.co
SUPABASE_KEY=tu_supabase_anon_key_aqui
```

> **⚠️ Importante**: Nunca commits el archivo `local.properties` al repositorio. Está incluido en `.gitignore`.

### 3. Construir la app
```bash
./gradlew build
```

### 4. Ejecutar en dispositivo/emulador
```bash
./gradlew installDebug
```

### 5. Configuración de Supabase (Backend)
La app utiliza Supabase como backend. Necesitas crear las siguientes tablas:

#### Tabla: `prices`
```sql
CREATE TABLE prices (
  id BIGSERIAL PRIMARY KEY,
  barcode TEXT NOT NULL,
  supermarket TEXT NOT NULL,
  price DECIMAL NOT NULL,
  product_name TEXT,
  brand TEXT,
  more_info TEXT,
  nickname TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

#### Tabla: `user_favorites`
```sql
CREATE TABLE user_favorites (
  id BIGSERIAL PRIMARY KEY,
  device_id TEXT NOT NULL,
  barcode TEXT NOT NULL,
  product_name TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

#### Tabla: `price_alerts`
```sql
CREATE TABLE price_alerts (
  id BIGSERIAL PRIMARY KEY,
  device_id TEXT NOT NULL,
  barcode TEXT NOT NULL,
  target_price DECIMAL,
  alert_percentage DECIMAL DEFAULT 10.0,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  last_alert_at TIMESTAMP WITH TIME ZONE
);
```

#### Tabla: `app_licenses`
```sql
CREATE TABLE app_licenses (
  device_id TEXT PRIMARY KEY,
  is_active BOOLEAN DEFAULT TRUE,
  nickname TEXT,
  last_used_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

## 📖 Uso de la aplicación

1. **Primera ejecución**: Completa el onboarding
2. **Escanea productos**: Usa el botón "Escanear producto" en la pantalla principal
3. **Registra precios**: Añade el precio actual y supermercado
4. **Compara ofertas**: Usa el comparador para buscar productos
5. **Configura alertas**: Añade productos a favoritos para recibir notificaciones

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! AhorrApp es un proyecto open source y cualquier ayuda es valiosa.

### Cómo contribuir:
1. **Fork** el repositorio
2. **Crea una rama** para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. **Commit** tus cambios (`git commit -m 'Añade nueva funcionalidad'`)
4. **Push** a la rama (`git push origin feature/nueva-funcionalidad`)
5. **Abre un Pull Request**

### Tipos de contribuciones:
- 🐛 **Reportar bugs**
- 💡 **Sugerir nuevas features**
- 📝 **Mejorar documentación**
- 🎨 **Diseño UI/UX**
- 🔧 **Mejoras técnicas**
- 🌐 **Traducciones**

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 🙏 Agradecimientos

- **OpenFoodFacts** por la API de información de productos
- **Supabase** por el backend como servicio
- **Google ML Kit** por el reconocimiento de códigos de barras
- **Comunidad open source** por las librerías utilizadas

## 📞 Contacto

**Desarrollado por [D4vRAM](https://github.com/D4vRAM369)**

- 🌐 **GitHub**: [github.com/D4vRAM369](https://github.com/D4vRAM369)
- ☕ **Buy me a coffee**: [buymeacoffee.com/D4vRAM369](https://www.buymeacoffee.com/D4vRAM369)
- 📧 **Email**: [Tu email si quieres compartirlo]

---

**Hecho con ❤️ en Gran Canaria para la comunidad canaria** 🇮🇨

*Si encuentras útil esta app, considera dejar una estrella ⭐ en GitHub o invitarme a un café ☕*