# Changelog

## [1.3.0] - 2026-02-26

### 2026-02-26
- **4fbf08b feat:** Sincronización masiva de pendientes desde el dashboard (botón en Home, estado `isSynced`, migración Room y conteo de pendientes)
- **445ee26 fix:** La sync de precios ya no se bloquea si falla el `upsert` de `products` en Supabase (se registra warning y continúa)
- **9846ff3 chore:** Limpieza del repositorio (salida de `.idea` del índice) y refuerzo de `.gitignore` (`.kotlin/`, `**/build/`)

#### Nuevas Funcionalidades
- Botón de sincronización masiva en el dashboard (Home), junto a ajustes
- Sincronización en lote de registros locales pendientes con Supabase
- Conteo de pendientes de sincronización visible en Home
- Estado de sincronización local por registro (`isSynced`) en Room

#### Cambios en Sincronización / Supabase
- La sincronización individual y masiva marca registros locales como sincronizados sin duplicar filas en el historial local
- La subida de precios intenta actualizar también la tabla `products` para mejorar la detección de productos al escanear
- Si falla `products`, la sincronización de `prices` continúa (mejor resiliencia)

#### Interfaz de Usuario
- Indicador visual de pendientes de sync en Home (`X pendientes` / `Sync OK`)
- Diálogo de confirmación y resumen de resultados para la sincronización masiva
- Estado visible en Historial (`sincronizado` / `pendiente de sync`) y desactivación del chip `Sync` cuando ya está sincronizado

#### Correcciones
- La comparativa comunitaria al escanear vuelve a cargarse automáticamente por código de barras sin depender del supermercado seleccionado
- Desacople entre historial comunitario por `barcode` y autocompletado de precio por supermercado

#### Mejoras Técnicas
- Migración Room `v2 -> v3` para añadir `isSynced` en `price_entries`
- Nuevas consultas DAO para pendientes (`count`, listado, marcado como sincronizado)
- Configuración local de Git y limpieza de artefactos de IDE/build para evitar ruido en el repositorio

---  

## [1.2.0] - 2026-01-20

### 2026-01-20
- **351702a feat:** Mejorar arquitectura de la app con optimización de código nativo (NDK, C++, CMake)
- **8e7d538 fix:** Solventado problema Edge‑to‑Edge que hacía que la pantalla colapsase con la barra de notificaciones
- **bf5ec58 merge:** Merge branch 'main' (actual release)

### 2026-01-10
- **3a788b2 feat:** Añadida política de privacidad oficial
- **d53bcd2 feat:** Sincronización de productos en Supabase y corrección de sintaxis onConflict

### 2026-01-09
- **3bec950 feat:** Pitido de escaneo y estadística térmica de precios; corrección de persistencia de nombre y zona
- **a6ffb3b feat:** Pequeños retoques y mejoras generales

### 2025-12-11
- **b57182f fix:** Corrección de exportación JSON y mejora de validación de datos

#### Nuevas Funcionalidades
- Sonido de escaneo
- Estadísticas térmicas de precios
- Sincronización de productos en Supabase
- Política de privacidad

#### Arquitectura
- Implementación de NDK (Native Development Kit) para mejor rendimiento y seguridad
- Integración de código nativo C++ mediante interfaz JNI
- Sistema de compilación mejorado con CMake
- Soporte multi‑arquitectura (ARM64‑v8a, ARMv7, x86, x86_64)
- Librerías nativas compiladas (`.so`) para protección mejorada del código

#### Interfaz de Usuario
- Optimización del layout de ProfileScreen (reducción de espaciado, eliminación de espacios innecesarios, reorganización, switch de sonido visible)
- Mejor aprovechamiento del espacio visual en todas las pantallas

#### Correcciones
- Fix crítico Edge‑to‑Edge
- Persistencia de datos
- Sintaxis onConflict
- Pequeños retoques y mejoras generales

#### Rendimiento
- Código base modernizado
- Optimización de gestión de recursos
- Mejoras en tiempos de respuesta
- Compilación nativa para mejor rendimiento

#### Mejoras Técnicas
- Configuración de NDK en el build
- Creación de interfaz JNI
- Optimización del proceso de compilación
- Mejora en sincronización de productos con la base de datos comunitaria
- Protección mejorada del código mediante compilación nativa

#### Documentación
- Añadida política de privacidad oficial
- Actualización de documentación técnica
- Mejoras en comentarios del código

---  

## [1.0.0] - 10-12-2025

### Added
- **Escáner de códigos de barras** integrado con ML Kit y CameraX
- **Registro de precios** por supermercado y producto
- **Base de datos comunitaria** usando Supabase
- **Sistema de alertas de precios** configurables
- **Productos favoritos** para seguimiento personalizado
- **Comparador de precios** inteligente
- **Historial personal** de aportaciones
- **Interfaz moderna** con Jetpack Compose y Material 3
- **Modo oscuro/claro** automático
- **Sistema de licencias** por dispositivo
- **Integración con OpenFoodFacts** para información de productos
- **Notificaciones push** para alertas de precios (WorkManager)
- **Onboarding** completo para nuevos usuarios

### Technical Features
- **Arquitectura MVVM** con ViewModels y LiveData/Flow
- **Room** para base de datos local
- **Retrofit + Gson** para APIs externas
- **Kotlin Coroutines** para operaciones asíncronas
- **Kotlin Serialization** para JSON
- **Navigation Compose** para navegación
- **Coil** para carga de imágenes
- **CI/CD** con GitHub Actions

### Security
- **Sin recopilación de datos personales**
- **Credenciales seguras** via local.properties
- **Licencia por dispositivo** para control de calidad

---  

## [1.1.0] - 11-12-2025

### Fixed
**CORREGIDO ERROR CRÍTICO QUE HACÍA QUE LA API DE OPENFOODFACTS NO RECONOCIERA MUCHOS ALIMENTOS POR EL PROCESO DE Minify Y ShrinkResources, QUE HAN SIDO DESACTIVADOS POR EL MOMENTO.**

### Added
- Device ID tracking
- Analytics foundation
- Enhanced data collection (device_id, nickname)
- Sistema de mensajes push completo
- Página de personalización en onboarding
- Animaciones de éxito en escáner
- Chip de ajustes avanzados
- Autoscroll inteligente
- Exportación de datos JSON
- Sistema de analytics avanzado

### Changed
- Database schema (nuevos campos en `prices`)
- Repository layer (uso de `device_id`)
- Onboarding mejorado (6 páginas)
- Interfaz de perfil (chip interactivo)
- Feedback visual (animaciones)
- Navegación (nueva pantalla de mensajes push)
- Backward compatibility mantenida

### Technical
- Supabase integration (nuevas tablas)
- Device identification (`Context.getDeviceId()`)
- Data models actualizados
- PushMessageScreen
- Analytics events
- Message views tracking
- Smart autoscroll
- JSON export/import

### Security
- Privacy enhanced
- No breaking changes
- JSON export/import
- Enhanced notifications

### UI/UX Improvements
- Animaciones de éxito
- Chip de ajustes
- Onboarding personalizado
- Mensajes push nativos
- Responsive design

---  

## [Unreleased]

### Planned
- Dashboard de analytics
- Sistema de autenticación
- Modo offline mejorado
- Gamificación
- Optimización de APK con minify
- Tests automatizados
- Screenshots en README
- Badges de build status
- Soporte multiidioma

---  

All notable changes to this project will be documented in this file.  
The format is based on Keep a Changelog, and this project adheres to Semantic Versioning.
