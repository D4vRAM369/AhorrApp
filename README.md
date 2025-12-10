# AhorrApp 💰📱🛒🇮🇨

<img width="512" height="512" alt="README_20251210130500824" src="https://github.com/user-attachments/assets/80c9698c-41eb-4eb9-9dd0-da8d35fcdfe5" />


![License: BUSL-1.1](https://img.shields.io/badge/License-BUSL--1.1-blue)
[![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?logo=android)](https://developer.android.com/) [![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-7F52FF?logo=kotlin)](https://kotlinlang.org/) [![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.0-4285F4?logo=android)](https://developer.android.com/jetpack/compose) [![Supabase](https://img.shields.io/badge/Supabase-2.0-3ECF8E?logo=supabase)](https://supabase.com/) [![OpenFoodFacts](https://img.shields.io/badge/OpenFoodFacts-API-00A65A)](https://world.openfoodfacts.org/) [![](https://img.shields.io/badge/Made_with_Love_&_Coffee-ff69b4)](https://img.shields.io/badge/Made_with_Love_&_Coffee-ff69b4) ![Project-Based Learning](https://img.shields.io/badge/Project--Based_Learning-FF8C00?style=flat)
 <a href="https://www.buymeacoffee.com/D4vRAM369"><img src="https://img.shields.io/badge/Buy_me_a_coffee-☕-5F7FFF"/></a> ![Gemini 3 Pro](https://img.shields.io/badge/Gemini_3_Pro-4285F4?logo=google&logoColor=white)
 [![ChatGPT by OpenAI](https://img.shields.io/badge/ChatGPT-74aa9c?logo=openai-icon&logoColor=white)](https://chat.openai.com/)
![Grok CodeFast-1](https://img.shields.io/badge/Grok-CodeFast--1-8A2BE2?logo=starship&logoColor=white)
 
 


![Antigravity IDE](https://img.shields.io/badge/Antigravity%20IDE-121212?style=for-the-badge&logo=jetbrains&logoColor=white)
![Gran Canaria](https://img.shields.io/badge/Gran%20Canaria-🇮🇨-FFDD00?style=for-the-badge)![Made by D4vRAM](https://img.shields.io/badge/Made%20by-D4vRAM369-006CFF?style=for-the-badge&logo=github&logoColor=white)

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

**AhorrApp** es una aplicación Android con un objetivo claro: **ahorrar en nuestras compras diarias**, **apuntando directamente a la especulación de las abusivas y continuas subidas de precios de los distintos supermercados de Las Palmas🇮🇨 (Canarias)**.

Soy consciente de que ésta situación no corresponde únicamente a Gran Canaria, **sino a todo el archipiélago canario.** Es por ello por lo que abro la posibilidad a que programadores más experimentados que yo, y/o que quieran colaborar en el proyecto, se pongan en contacto conmigo para ampliar la cobertura de precios y mejorar el proyecto en conjunto.

Obviamente para cualquier colaboración o tipo de cambios en el código fuente, **es imprescindible que contacten conmigo para participar en el proyecto como contribuidores**. Más información en el archivo *CONTRIBUTING.md*

Además de contactarme vía mensaje directo por Github, puedes contactarme vía Telegram como método de contacto principal.

**El enlace de contacto es t.me/D4vRAM369**

Es una aplicación comunitaria diseñada para ayudar a **ahorrar dinero** mediante la comparación de precios de productos de los distintos supermercados. Desarrollada en **Kotlin** y **Jetpack Compose**, esta herramienta permite escanear códigos de barras, registrar precios en diferentes supermercados, marcar productos como favoritos y tenerlos en una sección dedicada a ello. Incorporada también un sistema de notificaciones de alertas de bajadas de precios favoritos, pero aclaro que está en fase de prueba aún y el proyecto sigue y seguirá en desarrollo.

Creada como los demás proyectos que tengo con un método de estudio Project-Based Learning (PBL): aprender mientras creo y viceversa.

## Cómo usar AhorrApp y su funcionamiento

AhorrApp es muy sencilla de utilizar. Los pasos de funcionamiento son los siguientes:

 - **Escaneas el código de barras del producto** de precios entre supermercados
 - Si el producto está incluido en la API de **OpenFoodFacts**, se identificará automáticamente y solo debes **introducir el precio actual y el supermercado** en el dropdown (selector) para ello.
 - Al escanear y seleccionar el supermercado y precio, **se añadirá a tu base de datos local y también de forma automática a la base de datos comunitaria en Supabase**: **mientras mas participemos, más precisa y rica será la base de datos.**
 - Si sale una imágen con *"Error 404: artículo no encontrado"*, signfica que el producto no está incluido en la API de OpenFoodFacts, **no es problema de la app**. De hecho al salir esa imágen, la aplicación está funcionando perfectamente, ya que en su código las instrucciones son redirigir a esa imágen si no encuentra el código de barras en la API previamente mencionada.
 - **En esos caso, te saldrá un panel para rellenar los campos de nombre, marca e información adicional**, y tras guardar una vez, se **quedará guardado automáticamente tanto en tu base de datos local de la app, como en nuestra base de datos comunitaria** para futuros escaneos. 
 - **Los datos pasan a ser visibles por todos en tiempo real, con las últimas actualizaciones de precios.**


Recomiendo que añadan un nickname o nombre de pila al momento de la instalación (o desde "Perfil" dentro de "Historial de precios"), para hacer la base de datos más organizada.

Como alternativa o sugerencia, también pueden por ejemplo en lugar del nombre, introducir una **localización apróximada** en su lugar, si siempre compran en el mismo lugar *(ej: San Telmo, Santa Catalina, Guanarteme, etc.)*

### 🚀 Primeros Pasos

1. **Onboarding**: Sigue las instrucciones iniciales
2. **Licencia**: Registra tu dispositivo
3. **Permisos**: Autoriza el uso de la cámara

### 📱 Funcionalidades Principales

Explicadas en mayor profundidad arriba, especialmente lo de añadir productos nuevos cuando no estén en la API:

1. **Escanear Producto**
   - Abre la pantalla de escáner
   - Apunta la cámara al código de barras
   - La app reconocerá automáticamente el producto

2. **Registrar Precio**
   - Selecciona el supermercado actual
   - Ingresa el precio observado
   - Confirma para compartir con la comunidad

3. **Comparar Precios**
   - Ve todos los precios reportados para ese producto con interfaz térmica (distintos colores según el porcentaje de diferencia a la alza o a la baja)
   - Compara entre diferentes supermercados
   - Identifica las mejores ofertas

4. **Marcar Favoritos**
   - Toca el corazón en cualquier producto
   - Recibe alertas cuando baje el precio *(🚧 en fase de pruebas y desarrollo 🚧)*
   - Configura precios objetivo personalizados

5. **Ver Historial**
   - Revisa todas tus aportaciones y revisa en tus productos si alguien ha actualizado el precio.
   - Al entrar en un producto: se puede ver el día de la actualización con el nombre/nickname de la persona que lo escaneó por última vez.
   - Rastrea tus mejores compras
   - Analiza tus patrones de ahorro



## ¿Por qué usar AhorrApp?

### 💰 Ahorra Dinero Real

- **Comparación inteligente** de precios que indica diferencias entre precios de distintos supermercados en el producto seleccionado.
- **Historial personal** para rastrear tus mejores compras
- **Base de datos comunitaria** con precios reales de usuarios
- **Alertas automáticas** cuando bajan los precios de tus productos favoritos ***🚧 EN FASE DE PRUEBA Y DESARROLLO 🚧***

### 📱 Experiencia Moderna y Fluida

- **Interfaz intuitiva** con Jetpack Compose y Material Design 3
- **Escáner avanzado** de códigos de barras con ML Kit
- **Modo oscuro/claro** automático
- **Onboarding completo** para nuevos usuarios *(5 pantallas que solo aparecen en el momento de la instalación)*

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

### 🔔 Sistema de Alertas Avanzado *(En fase de pruebas aún)*

- **Alertas configurables** para productos favoritos
- **Notificaciones push** automáticas con WorkManager
- **Seguimiento inteligente** de cambios de precio
- **Precios objetivo** personalizables

### ⭐ Funcionalidades Premium

- **Productos favoritos** para seguimiento personalizado
- **Comparador inteligente** entre múltiples supermercados
- **Interfaz moderna** y responsive

### 🔒 Privacidad y Seguridad

- **Datos anónimos** en la base comunitaria
- **Licencias por dispositivo** justas
- **Sin trackers** ni recopilación innecesaria
- **Código abierto** y auditable

---

## 📸 Capturas de Pantalla

Un recorrido visual por las principales pantallas de la app. Tamaño reducido (**140px**) para un efecto collage limpio.


## 🚀 Onboarding (Instalación)

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/68ffc02f-87ef-4611-91db-ea9ef4189e18" width="140"/>
      <br/><sub>Bienvenida</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/ea40f3fd-46ae-4b5c-b24a-f88a8cdddcff" width="140"/>
      <br/><sub>Onboarding 1</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/3748fe8b-5af2-4cd8-bc65-e9373b16c34b" width="140"/>
      <br/><sub>Onboarding 2</sub>
    </td>
  </tr>

  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/713a69f0-d043-4d18-b84a-056f07076324" width="140"/>
      <br/><sub>Onboarding 3</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/244857db-eef8-434d-bb7e-bd0acdd1027f" width="140"/>
      <br/><sub>Onboarding 4</sub>
    </td>
  </tr>
</table>

---

## 🌙 Bienvenida (Modo Claro & Oscuro)

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/edea1603-d44d-46c9-8b50-e727366a0f37" width="140"/>
      <br/><sub>Bienvenida clara</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/183f0a7a-680f-4bda-b68f-164fa1bc1f6a" width="140"/>
      <br/><sub>Bienvenida clara 2</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/1c5f576c-39c7-41ab-a840-4b8ff539acc5" width="140"/>
      <br/><sub>Bienvenida oscura</sub>
    </td>
  </tr>

  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/756341be-27cb-411e-a2c9-6d828ab16cf7" width="140"/>
      <br/><sub>Bienvenida oscura 2</sub>
    </td>
  </tr>
</table>

---

## 📚 Historial

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/db009d2c-d4f8-449a-aa6a-0ec2e0a03121" width="140"/>
      <br/><sub>Historial 1</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/0a0732bd-f1b5-4a01-8edb-5577f1f53db5" width="140"/>
      <br/><sub>Historial 2</sub>
    </td>
  </tr>
</table>

---

## 📝 Gestión de productos

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/15ff6a44-7236-49a2-8623-42c4e1489e02" width="140"/>
      <br/><sub>Editar información</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/65e0df07-fc80-4679-8e2d-65a0eeefd7c2" width="140"/>
      <br/><sub>Error: no encontrado</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/109ba56d-ff9f-433f-a186-ec7d4889b3bb" width="140"/>
      <br/><sub>Añadir producto</sub>
    </td>
  </tr>

  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/d8cd749d-ea93-4609-86b4-bebffe5c7b63" width="140"/>
      <br/><sub>Editar producto</sub>
    </td>
  </tr>
</table>

---

## 💸 Gestión de precios

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/943a2db3-b6ed-405e-87bc-5518b4d57c53" width="140"/>
      <br/><sub>Añadir / actualizar precio</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/860e85f2-d8e1-4115-9e02-36372946cc13" width="140"/>
      <br/><sub>Dropdown supermercados</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/d3e68043-d694-4551-ac6a-c7fb990c7a04" width="140"/>
      <br/><sub>Perfil y ajustes</sub>
    </td>
  </tr>
</table>

---

## ⚖️ Comparador de precios

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/4f141229-ceff-472c-8dae-2360ef7c506f" width="140"/>
      <br/><sub>Comparador</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/3bed97e8-e9a7-4638-9123-14530fb55db8" width="140"/>
      <br/><sub>Comparativa detallada</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/18502850-9b56-4c6c-8f2b-df202c0d3d74" width="140"/>
      <br/><sub>Favoritos</sub>
    </td>
  </tr>

  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/e3886cd5-8239-4f03-a0db-c89b0c8fc84e" width="140"/>
      <br/><sub>Alertas de precio (testing)</sub>
    </td>
  </tr>
</table>

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
2. **Licencia**: Registra tu dispositivo para acceder a todas las funciones _(es automático con la instalación)_
3. **Permisos**: Concede acceso a la cámara para escanear productos
4. **Sincronización**: Los datos comunitarios se descargarán automáticamente

## Cómo Usar AhorrApp


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

¿Quieres mejorar AhorrApp? Las contribuciones son bienvenidas, **pero es imprescindible seguir las reglas del archivo CONTRIBUTING.md**:

1. Fork del repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Sigue las convenciones de Kotlin/Android
4. Envía tu PR con descripción detallada y contacta conmigo

### Guías de Contribución
- Consulta [CONTRIBUTING.md](CONTRIBUTING.md) para detalles
- Sigue el estilo de código establecido
- Añade tests para nuevas funcionalidades
- Documenta cambios significativos

## 📄 Licencia

Este proyecto está bajo la **Licencia BUSL-1.1** - ver el archivo [LICENSE](LICENSE) para más detalles.

## 🙏 Agradecimientos

- **[OpenFoodFacts](https://world.openfoodfacts.org/)** por la base de datos nutricional
- **[Supabase](https://supabase.com/)** por el backend comunitario
- **[Google ML Kit](https://developers.google.com/ml-kit)** por el reconocimiento de códigos
- **Comunidad Android** por las librerías y herramientas
- **Contribuidores** que hacen y harán posible este proyecto con su continuidad

## 💬 Soporte al Proyecto

Si AhorrApp te ayuda a ahorrar dinero y tomar mejores decisiones de compra, considera apoyar el proyecto:

[![GitHub stars](https://img.shields.io/github/stars/D4vRAM369/ahorrapp?style=social)](https://github.com/D4vRAM369/ahorrapp/stargazers) [![Buy me a coffee](https://img.shields.io/badge/Buy_me_a_coffee-FFDD00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black)](https://www.buymeacoffee.com/D4vRAM369)

---

*Desarrollado por D4vRAM con ❤️ y ☕ para la comunidad de Canarias que está harta de la tiranía de los precios abusivos a la que nos vemos sometidos.*

**Es hora de decir BASTA**. Descarga AhorrApp y únete a la comunidad. Te necesitamos, nos necesitamos.
