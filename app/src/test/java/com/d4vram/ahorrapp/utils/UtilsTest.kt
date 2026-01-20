package com.d4vram.ahorrapp.utils

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests para Utils
 */
class UtilsTest {

    @Test
    fun isValidPrice_withValidPrice_returnsTrue() {
        assertTrue(Utils.isValidPrice(10.99))
        assertTrue(Utils.isValidPrice(0.01))
        assertTrue(Utils.isValidPrice(9999.99))
    }

    @Test
    fun isValidPrice_withInvalidPrice_returnsFalse() {
        assertFalse(Utils.isValidPrice(0.0))
        assertFalse(Utils.isValidPrice(-1.0))
        assertFalse(Utils.isValidPrice(10000.0))
        assertFalse(Utils.isValidPrice(10001.0))
    }

    @Test
    fun formatPrice_formatsCorrectly() {
        assertEquals("10.99", Utils.formatPrice(10.99))
        assertEquals("0.01", Utils.formatPrice(0.01))
        assertEquals("100.00", Utils.formatPrice(100.0))
    }
}