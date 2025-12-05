# Walkthrough: Gestión de Historial, Perfil y Sincronización en AhorrApp

Este documento detalla las nuevas funcionalidades implementadas para la gestión avanzada de datos locales, perfil de usuario y sincronización con la nube.

## 1. Gestión de Historial (Offline-First)
La pantalla de historial ha evolucionado de una simple lista de lectura a un panel de control y gestión.

### Funcionalidades
*   **Edición Local (Lápiz):**
    *   Permite corregir errores tipográficos en el nombre del producto o ajustar el precio si se introdujo mal.
    *   **Acción:** Al guardar, se actualiza inmediatamente la base de datos local (`Room`).
    *   *Nota:* Esto no cambia los datos en la nube automáticamente para evitar inconsistencias rápidas.
*   **Sincronización Selectiva (Nube/Sync):**
    *   Si has editado un producto en local y quieres que esa corrección se refleje para todos, usa el botón "Sync" (Verde).
    *   **Seguridad:** Muestra un diálogo de responsabilidad ("Asegúrate de que sean verídicos...") antes de enviar.
*   **Borrado Local (Papelera):**
    *   Elimina el registro de tu dispositivo.
    *   Útil para limpriar pruebas o errores de escaneo. No afecta a la base de datos comunitaria.

## 2. Perfil y Ajustes
Accesible mediante el icono de engranaje (⚙️) en la pantalla de Historial.

### Funcionalidades
*   **Identidad:** Campo para establecer tu "Nombre de usuario". Este dato se guarda localmente (y se puede incluir en envíos futuros).
*   **Estadísticas:**
    *   **Productos añadidos:** Contador total de tus escaneos.
    *   **Precios compartidos:** Contador de precios válidos enviados.
*   **Exportación de Datos (CSV):**
    *   Genera un archivo `historial_precios.csv` compatible con Excel, Google Sheets y otras herramientas.
    *   **Formato:** Estándar RFC 4180 (`text/csv`).
    *   **Compartir:** Usa el menú nativo de Android para enviar el archivo por Telegram, WhatsApp, Email o guardar en Drive.

## 3. Flujo de Datos (Backend Readiness)
Siguiendo tus requisitos, la aplicación ahora envía **todo** el paquete de información al servidor.

*   **Payload Completo:** Al guardar un precio (o al re-sincronizar), se envían:
    *   Código de barras
    *   Precio y Supermercado
    *   Nombre del producto
    *   Marca
    *   Más información (formato, peso, etc.)
*   **Lógica de "Más barato/Más caro":** Tal como solicitaste, esta lógica está delegada en el backend. La app provee la materia prima (precios y fechas) para que el servidor calcule ranking y comparativas.

## 4. Estructura Técnica
*   **Base de Datos Local:** Room Database con tablas actualizadas para soportar operaciones CRUD.
*   **Provider:** Configurado `FileProvider` seguro en `AndroidManifest` para permitir la salida de archivos CSV desde la app hacia otras aplicaciones.
*   **Navegación:** Unificada. "Mis productos" y "Historial" ahora convergen en una única experiencia de usuario coherente en `HistoryScreen`.

---
**Estado Actual:** La aplicación está lista para compilar ("Build") y usar. Todas las funcionalidades solicitadas en la sesión están implementadas en código Kotlin/Compose.
