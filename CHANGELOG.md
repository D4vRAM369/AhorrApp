# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-01-XX

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

## [Unreleased]

### Planned
- Screenshots en README
- Badges de build status
- Tests instrumentados completos
- Documentación de API
- Optimizaciones de rendimiento
- Soporte multiidioma

---

## Development Notes

- **Initial commit**: Proyecto base con estructura Android
- **Database integration**: Implementación de Supabase
- **UI overhaul**: Migración completa a Compose
- **Feature complete**: Todas las funcionalidades core implementadas
- **Production ready**: Build limpio, tests básicos, CI configurado