# Versión mínima de CMake requerida
cmake_minimum_required(VERSION 3.22.1)

# Nombre del proyecto
project("ahorrapp_keys")

# Crear la librería compartida (.so)
add_library(
        # Nombre de la librería (se generará libahorrapp_keys.so)
        ahorrapp_keys
        
        # Tipo: SHARED = librería dinámica (.so)
        SHARED
        
        # Archivos fuente a compilar
        keys.cpp
)

# Buscar la librería de logging de Android
find_library(
        # Variable donde se guardará la ruta
        log-lib
        
        # Nombre de la librería NDK a buscar
        log
)

# Enlazar librerías necesarias
target_link_libraries(
        # Nuestra librería
        ahorrapp_keys
        
        # Librería de logging (opcional, útil para debug)
        ${log-lib}
)
