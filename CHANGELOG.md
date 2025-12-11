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


### Fixed

**CORREGIDO ERROR CRÍTICO QUE HACÍA QUE LA API DE OPENFOODFACTS NO RECONOCIERA MUCHOS ALIMENTOS POR EL PROCESO DE Minify Y ShrinkResources, QUE HAN SIDO DESACTIVADOS POR EL MOMENTO.**

## **RECOMIENDO ENCARECIDAMENTE LA DESCARGA DE LA NUEVA VERSIÓN 1.1 PARA UNA MEJOR FUNCIONALIDAD DE LA APLICACIÓN**

Además de ésta corrección crítica, varios cambios han sido añadidos:

### Added
- **Device ID tracking** - Mejor identificación de usuarios por dispositivo
- **Analytics foundation** - Base de datos preparada para métricas de usuarios
- **Enhanced data collection** - Campos device_id y nickname en precios
- **Sistema de mensajes push completo** - Comunicación directa con usuarios desde Supabase
- **Página de personalización en onboarding** - Campos nickname y zona de compra en OnboardingPage6
- **Animaciones de éxito en escáner** - Feedback visual con círculo verde al detectar códigos
- **Chip de ajustes avanzados** - Menú desplegable con opciones de exportación e importación
- **Autoscroll inteligente** - Solo activa cuando hay contenido que no cabe en pantalla
- **Exportación de datos JSON** - Historial completo exportable en formato estructurado
- **Sistema de analytics avanzado** - Tracking completo de eventos y métricas de usuario

### Changed
- **Database schema** - Nuevos campos en tabla prices para mejor tracking
- **Repository layer** - Actualizado para incluir device_id en inserciones
- **Onboarding mejorado** - 6 páginas en lugar de 5, con personalización final
- **Interfaz de perfil** - Nueva sección de ajustes con chip interactivo
- **Feedback visual** - Animaciones y transiciones más fluidas
- **Navegación** - Nueva ruta para pantalla de mensajes push
- **Backward compatibility** - Mantenida compatibilidad total con v1.0

### Technical
- **Supabase integration** - Nuevas tablas: users, user_analytics, analytics_events, push_messages, message_views
- **Device identification** - Función Context.getDeviceId() implementada
- **Data models** - SupabasePriceEntry actualizado con device_id
- **PushMessageScreen** - Nueva pantalla dedicada para mensajes del sistema
- **Analytics events** - Tracking de escaneos, favoritos, alertas y navegación
- **Message views tracking** - Registro de interacciones con mensajes push
- **Smart autoscroll** - Detección automática de overflow de contenido
- **JSON export/import** - Funcionalidad completa de gestión de datos

### Security
- **Privacy enhanced** - Mejor control de datos por dispositivo
- **No breaking changes** - Compatibilidad total con versiones anteriores
- **JSON export/import** - Funcionalidad completa de gestión de datos
- **Enhanced notifications** - Mejor diseño responsive y estado visual

### UI/UX Improvements
- **Animaciones de éxito** - Feedback satisfactorio al escanear productos
- **Chip de ajustes** - Acceso rápido a funcionalidades avanzadas
- **Onboarding personalizado** - Experiencia de bienvenida más completa
- **Mensajes push nativos** - Integración perfecta con la app
- **Responsive design** - Mejor adaptación a diferentes tamaños de pantalla

## [Unreleased]

### Planned
- Dashboard de analytics con gráficos visuales (base implementada)
- Sistema de autenticación de usuarios
- Modo offline mejorado
- Gamificación con puntos y logros
- API pública para integraciones de terceros
- Optimización de APK con minify activado
- Tests automatizados completos
- Screenshots en README
- Badges de build status
- Soporte multiidioma

---

## Development Notes

- **Initial commit**: Proyecto base con estructura Android
- **Database integration**: Implementación de Supabase
- **UI overhaul**: Migración completa a Compose
- **Feature complete**: Todas las funcionalidades core implementadas
- **v1.1.0 release**: Sistema de device tracking y analytics foundation
- **Push messaging system**: Comunicación directa con usuarios implementada
- **Onboarding enhancement**: Página de personalización y autoscroll inteligente
- **UI animations**: Feedback visual mejorado con animaciones de éxito
- **Advanced settings**: Chip de ajustes con exportación JSON
- **Analytics tracking**: Sistema completo de eventos y métricas
- **Production ready**: Build limpio, tests básicos, CI configurado