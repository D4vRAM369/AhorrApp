# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

## [1.1.0] - 11-12-2025

### Added
- **Device ID tracking** - Mejor identificación de usuarios por dispositivo
- **Analytics foundation** - Base de datos preparada para métricas de usuarios
- **Enhanced data collection** - Campos device_id y nickname en precios

### Changed
- **Database schema** - Nuevos campos en tabla prices para mejor tracking
- **Repository layer** - Actualizado para incluir device_id en inserciones
- **Backward compatibility** - Mantenida compatibilidad total con v1.0

### Technical
- **Supabase integration** - Nuevas tablas: users, user_analytics, analytics_events
- **Device identification** - Función Context.getDeviceId() implementada
- **Data models** - SupabasePriceEntry actualizado con device_id

### Security
- **Privacy enhanced** - Mejor control de datos por dispositivo
- **No breaking changes** - Compatibilidad total con versiones anteriores

## [Unreleased]

### Planned
- Screenshots en README
- Badges de build status
- Soporte multiidioma
- Dashboard de analytics
- Sistema de autenticación de usuarios

---

## Development Notes

- **Initial commit**: Proyecto base con estructura Android
- **Database integration**: Implementación de Supabase
- **UI overhaul**: Migración completa a Compose
- **Feature complete**: Todas las funcionalidades core implementadas
- **Production ready**: Build limpio, tests básicos, CI configurado