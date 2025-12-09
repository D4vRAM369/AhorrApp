# 🤝 Guía para Contribuir a AhorrApp

¡Gracias por tu interés en contribuir a AhorrApp! Este proyecto es un ejemplo de **Project-Based Learning (PBL)** y estamos encantados de recibir contribuciones de la comunidad.

## 🚀 Cómo Empezar

### 1. **Fork y Clone**
```bash
git clone https://github.com/YOUR_USERNAME/AhorrApp.git
cd AhorrApp
```

### 2. **Configuración del Entorno**
- **Android Studio**: Arctic Fox o superior
- **JDK**: 11 o superior
- **Supabase Account**: Para testing de backend

### 3. **Configurar Credenciales**
Crea un archivo `local.properties` en la raíz:
```properties
SUPABASE_URL=https://tu-proyecto.supabase.co
SUPABASE_KEY=tu-anon-key-publica
```

## 🛠️ Desarrollo

### **Estructura del Proyecto**
```
app/src/main/java/com/d4vram/ahorrapp/
├── data/           # Repositorios, modelos, base de datos
├── ui/screens/     # Pantallas de Compose
├── viewmodel/      # ViewModels (lógica de negocio)
├── navigation/     # Navegación de la app
└── workers/        # WorkManager (tareas en background)
```

### **Convenciones de Código**
- **Kotlin**: Usa el estilo oficial de Kotlin
- **Compose**: Funciones composables con nombres descriptivos
- **MVVM**: Separa claramente UI, lógica y datos
- **Commits**: Mensajes descriptivos en español/inglés

### **Testing**
```bash
# Ejecutar tests unitarios
./gradlew test

# Ejecutar tests de instrumentación
./gradlew connectedAndroidTest
```

## 📋 Tipos de Contribuciones

### 🐛 **Reportar Bugs**
- Usa la plantilla de issue para bugs
- Incluye: pasos para reproducir, versión de Android, logs

### 💡 **Nuevas Features**
- Discute ideas primero en Discussions
- Implementa siguiendo la arquitectura existente
- Añade tests para nuevas funcionalidades

### 📚 **Documentación**
- Mejora el README.md
- Añade comentarios en código complejo
- Traducciones al inglés/español

### 🎨 **UI/UX**
- Sigue Material Design 3
- Considera accesibilidad
- Testea en múltiples tamaños de pantalla

## 🔄 Pull Requests

### **Proceso**
1. **Fork** el repositorio
2. **Crea una branch** descriptiva: `feature/nueva-funcionalidad`
3. **Implementa** tus cambios
4. **Añade tests** si es necesario
5. **Commit** con mensajes claros
6. **Push** y crea el PR

### **Plantilla de PR**
```markdown
## Descripción
Breve descripción de los cambios

## Tipo de Cambio
- [ ] Bug fix
- [ ] Nueva funcionalidad
- [ ] Mejora de documentación
- [ ] Refactorización

## Testing
- [ ] Tests unitarios pasan
- [ ] Testeado en dispositivo/emulador
- [ ] No rompe funcionalidad existente

## Screenshots (si aplica)
[Incluye capturas de pantalla]
```

## 🎯 Áreas de Alto Impacto

### **Prioridad Alta**
- **Testing**: Más tests unitarios e instrumentación
- **Performance**: Optimización de consultas y UI
- **Offline Mode**: Mejorar funcionalidad sin conexión

### **Features Solicitadas**
- **Analytics**: Dashboard de ahorro personal
- **Maps**: Integración con Google Maps
- **Widgets**: Pantalla principal
- **Dark Mode**: Mejorar implementación actual

## 📞 Contacto

- **Issues**: Para bugs y features
- **Discussions**: Para preguntas generales
- **Discord**: [Enlace si existe]

## 📜 Licencia

Al contribuir, aceptas que tu código será bajo **GPLv3**.

---

¡Gracias por hacer que AhorrApp sea mejor! Cada contribución cuenta en nuestro viaje de aprendizaje. 🚀