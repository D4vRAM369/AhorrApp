# Guía de Contribución

¡Gracias por tu interés en contribuir a AhorrApp! 🎉

Este documento explica cómo puedes contribuir al proyecto de manera efectiva.

## 🚀 Cómo empezar

### 1. Fork y Clone
```bash
git clone https://github.com/tu-usuario/AhorrApp.git
cd AhorrApp
```

### 2. Configurar el entorno
Sigue las instrucciones de instalación en el [README.md](README.md).

### 3. Crear una rama
```bash
git checkout -b feature/nueva-funcionalidad
```

## 📝 Tipos de contribuciones

### 🐛 Reportar bugs
- Usa las plantillas de issue disponibles
- Incluye pasos para reproducir
- Añade capturas de pantalla si es relevante

### 💡 Sugerir nuevas features
- Describe el problema que resuelve
- Explica la implementación propuesta
- Considera el impacto en la UX

### 🔧 Contribuciones técnicas
- **Código**: Sigue las convenciones de Kotlin/Android
- **Tests**: Añade tests para nuevas funcionalidades
- **Documentación**: Actualiza README y comentarios

### 🎨 Diseño y UX
- Mejoras en la interfaz
- Nuevos iconos o assets
- Optimizaciones de rendimiento

## 📋 Estándares de código

### Kotlin
- Usa `val` en lugar de `var` cuando sea posible
- Nombres descriptivos para variables y funciones
- Comentarios en español para código complejo

### Compose
- Usa `remember` apropiadamente
- Evita efectos secundarios en composables
- Mantén la lógica de UI separada

### Commits
```bash
git commit -m "feat: añadir funcionalidad de escaneo mejorado

- Mejora la detección de códigos de barras
- Añade feedback visual al usuario
- Fixes #123"
```

## 🧪 Testing

### Ejecutar tests
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
```

### Añadir tests
- Tests unitarios para lógica de negocio
- Tests de UI para componentes Compose
- Tests de integración para flujos completos

## 📄 Pull Requests

### Checklist antes de enviar
- [ ] Código compila sin errores
- [ ] Tests pasan
- [ ] Lint pasa (`./gradlew lint`)
- [ ] Documentación actualizada
- [ ] Commits siguen el formato conventional

### Descripción del PR
```
## Descripción
Breve explicación de los cambios

## Tipo de cambio
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
Cómo probar los cambios

## Screenshots
Si aplica, añadir capturas
```

## 🤝 Código de conducta

- Sé respetuoso con otros contribuidores
- Mantén un ambiente positivo
- Ayuda a revisar PRs de otros

## 📞 Contacto

- **Issues**: [GitHub Issues](https://github.com/D4vRAM369/AhorrApp/issues)
- **Discussions**: Para preguntas generales
- **Email**: Para asuntos privados

---

¡Tu contribución hace la diferencia! Cada aportación, por pequeña que sea, ayuda a mejorar AhorrApp para la comunidad canaria. 🇮🇨