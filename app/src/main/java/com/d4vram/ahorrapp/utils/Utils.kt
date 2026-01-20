package com.d4vram.ahorrapp.utils

/**
 * Utilidades para validación de datos
 */
object Utils {

    /**
     * Valida si un precio es válido (positivo y razonable)
     */
    fun isValidPrice(price: Double): Boolean {
        return price > 0 && price < 10000 // Máximo 9999.99 para evitar errores
    }

    /**
     * Formatea precio con 2 decimales
     */
    fun formatPrice(price: Double): String {
        return String.format(java.util.Locale.US, "%.2f", price)
    }
}