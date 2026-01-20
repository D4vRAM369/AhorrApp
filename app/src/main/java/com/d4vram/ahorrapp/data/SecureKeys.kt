package com.d4vram.ahorrapp.data

/**
 * Objeto singleton que proporciona acceso seguro a las claves API
 * almacenadas en código nativo C++
 * 
 * Las claves ya NO están en BuildConfig, sino compiladas en libahorrapp_keys.so
 * Esto dificulta significativamente la ingeniería inversa del APK
 */
object SecureKeys {
    
    /**
     * Bloque de inicialización estático
     * Se ejecuta cuando se carga la clase por primera vez
     */
    init {
        // Cargar la librería nativa
        // Busca el archivo libahorrapp_keys.so en las librerías nativas del APK
        System.loadLibrary("ahorrapp_keys")
    }
    
    /**
     * Función nativa que obtiene la URL de Supabase
     * La implementación real está en keys.cpp
     * 
     * @return URL de Supabase
     */
    external fun getSupabaseUrl(): String
    
    /**
     * Función nativa que obtiene la clave API de Supabase
     * La implementación real está en keys.cpp
     * 
     * @return Clave API de Supabase (anon key)
     */
    external fun getSupabaseKey(): String
}
