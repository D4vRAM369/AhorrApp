<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_background.xml" width="200" height="200" alt="AhorrApp Icon" /> <!-- Reemplaza con ruta real al logo si disponible --><br><br>
   <b>🛒 AhorrApp v1.1 🇮🇨</b><br><br>
  <img src="https://img.shields.io/badge/Kotlin-1.9%2B-orange"/>
  <img src="https://img.shields.io/badge/Android-10%2B-brightgreen?logo=android"/>
  <img src="https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?logo=jetpackcompose"/>
  <img src="https://img.shields.io/badge/Supabase-Backend-3ECF8E?logo=supabase"/>
  <img src="https://img.shields.io/badge/Room-Database-green?logo=sqlite"/>
  <img src="https://img.shields.io/badge/License-GPLv3-green"/>
  <img src="https://img.shields.io/badge/Made_with-Gemini_&_PBL-8A2BE2?logo=google-gemini&logoColor=white"/>
  <img src="https://img.shields.io/badge/Clean_Architecture-MVVM-blueviolet"/>
  <img src="https://img.shields.io/badge/Optimized_for-Smart_Savings-gold"/>
  <img src="https://img.shields.io/badge/Made_with-Love_&_Code-ff69b4"/>
  <a href="https://www.buymeacoffee.com/D4vRAM369"><img src="https://img.shields.io/badge/Buy_me_a_coffee-☕-5F7FFF"/></a>
</p>

---

## ✨ Nuevo en v1.1 (MVP+)

* **Comparador de Precios Inteligente**: Visualización térmica de precios (sistema de colores tipo semáforo) para identificar de un vistazo las mejores ofertas entre supermercados de Canarias.
* **Sistema de Identidad & Seguridad (ABP Security)**:
  * **Licencias por Dispositivo**: Control de acceso robusto validado contra Supabase mediante IDs únicos de hardware (`ANDROID_ID`), permitiendo gestión remota.
  * **Pantalla de Bloqueo Remota**: Kill-switch administrativo para mantenimiento, actualizaciones obligatorias o revocación de acceso.
  * **Firmas de Autoría**: Cada precio registrado queda firmado inmutablemente con el nickname del usuario.
* **Gestión de Perfil Persistente**: Edición de nombre de usuario con validación visual y sincronización en la nube.
* **Historial Híbrido**: Respaldo local con capacidades de Exportación e Importación CSV para control total de tus datos.
* **Navegación Fluida**: Nuevo "Modo Lista" en el comparador para exploración rápida de todo el catálogo disponible.
* **🔔 Sistema de Alertas de Precios** (NUEVO):
  * **Productos Favoritos**: Guarda productos para seguimiento personalizado.
  * **Alertas Inteligentes**: Notificaciones automáticas cuando bajan los precios (configurable por porcentaje o precio objetivo).
  * **WorkManager Integration**: Verificación automática en background cada 6 horas.
  * **Notificaciones Push**: Alertas nativas de Android con detalles de ahorro.

---

## 📊 Estado del Proyecto

### ✅ **Funcionalidades Completadas**
- **Core MVP**: Escaneo, comparación y sincronización básica ✅
- **Sistema de Alertas**: Implementado pero requiere testing en dispositivo ✅
- **Arquitectura**: MVVM + Clean Architecture ✅
- **UI/UX**: Jetpack Compose moderno ✅

### 🚧 **En Desarrollo / Testing**
- **Alertas de Precios**: Código listo, necesita validación en dispositivo real
- **WorkManager**: Implementado, requiere pruebas de funcionamiento
- **Notificaciones**: Sistema básico implementado

### 🎯 **Próximas Features Planificadas**
- **Analytics de Ahorro**: Dashboard con estadísticas personales
- **Mapas de Tiendas**: Ubicaciones GPS + precios en tiempo real
- **Widgets**: Pantalla principal con precios destacados
- **Modo Offline Premium**: Caché inteligente

### ⚠️ **Notas Importantes**
- **MVP Stage**: Proyecto funcional pero en fase de testing
- **Testing Necesario**: Alertas requieren validación en dispositivo real
- **Documentación**: README actualizado, pero falta documentación técnica detallada

---

## 🚀 Uso

1. **Escanea**: Utiliza el escáner integrado para leer el código de barras de cualquier producto.
2. **Aporta**: Si el producto es nuevo o el precio ha cambiado, regístralo seleccionando el supermercado. La app sincronizará tu aporte firmado.
3. **Compara**:
   * Usa el buscador inteligente o activa el **"Modo Lista"**.
   * Identifica rápidamente los chollos: Tarjetas amarillas (Mejor precio) vs Rojas (+50%).
4. **Gestiona**:
   * Accede a tu **Perfil ⚙️** desde la pantalla de Historial.
   * Configura tu identidad comunitaria.
   * Haz copias de seguridad de tus registros.

---
 
 ## 🛠️ Configuración de Desarrollo
 
 Para compilar y ejecutar este proyecto, necesitas configurar las credenciales de Supabase:
 
 1. Crea un archivo `local.properties` en la raíz del proyecto (si no existe).
 2. Agrega tus claves de Supabase (puedes obtenerlas siguiendo `supabase_setup.md`):
 
 ```properties
 SUPABASE_URL="https://tu-proyecto.supabase.co"
 SUPABASE_KEY="tu-anon-key-publica"
 ```
 
 > **Nota**: El archivo `local.properties` está excluido de git por seguridad. Nunca subas tus claves reales al repositorio.
 
 ---
 
 ## ⚙️ Arquitectura & Stack

Este proyecto demuestra una arquitectura **MVVM (Model-View-ViewModel)** robusta y escalable:

* **UI**: Desarrollada 100% en **Jetpack Compose**.
* **Persistencia Local**: **Room Database** para funcionamiento offline-first.
* **Cloud & Realtime**: **Supabase** (PostgreSQL) para la sincronización de precios y gestión de licencias.
* **Datos Externos**: Integración con **OpenFoodFacts API** para metadatos e imágenes de productos.
* **Concurrencia**: Kotlin Coroutines & StateFlow para reactividad en tiempo real.

---

## 🛠️ Implementación Técnica (ABP)

* **Seguridad Desacoplada**: El sistema de licencias reside en el `Repository` y es observado reactivamente por la UI, permitiendo bloqueos en caliente sin lógica compleja en las vistas.
* **Sincronización Resiliente**: Manejo de errores con `runCatching` y logs detallados para depuración, asegurando que la app funcione incluso con conectividad intermitente.
* **Retrocompatibilidad**: Uso de `SimpleDateFormat` y APIs estándar para asegurar funcionamiento en un amplio rango de versiones de Android.

---

## 📚 Notas del Desarrollador

AhorrApp es un proyecto nacido del método **Project-Based Learning (PBL)**, con el objetivo de combatir la inflación mediante la colaboración comunitaria, aplicando técnicas avanzadas de desarrollo Android asistido por IA.

---

## 💬 Soporte al Proyecto

Si AhorrApp te ayuda a estirar tu presupuesto, considera dejar una estrella o invitarme a un café:

<p align="center">
  <a href="https://github.com/D4vRAM369/AhorrApp/stargazers">
    <img src="https://img.shields.io/badge/Give_a_Star_on_GitHub-⭐-yellow?style=for-the-badge"/>
  </a>
  <a href="https://www.buymeacoffee.com/D4vRAM369">
    <img src="https://img.shields.io/badge/Buy_me_a_coffee-☕-blueviolet?style=for-the-badge"/>
  </a>
</p>

---

💡 *Desarrollado por D4vRAM mediante aprendizaje PBL e IA colaborativa.*  
💚 Licencia: **GPLv3 – Software libre, código abierto y transparente.**
