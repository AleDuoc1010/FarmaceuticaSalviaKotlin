package com.example.farmaceuticasalvia.domain.validation

import org.junit.Assert.*
import org.junit.Test

class ProductValidatorTest {

    @Test
    fun `validateQuantity retorna error si esta vacio`() {
        val result = validateQuantity("")
        assertEquals("la cantidad es obligatoria", result)
    }

    @Test
    fun `validateQuantity retorna error si contiene letras`() {
        val result = validateQuantity("10a")
        assertEquals("Solo números", result)
    }

    @Test
    fun `validateQuantity retorna error si es 0`() {
        val result = validateQuantity("0")
        assertEquals("La cantidad no puede ser menor a 1", result)
    }

    @Test
    fun `validateQuantity retorna error si es negativo`() {
        // El check de isDigit() atrapa el signo menos "-", así que devuelve "Solo números"
        val result = validateQuantity("-5")
        assertEquals("Solo números", result)
    }

    @Test
    fun `validateQuantity retorna null si es valido`() {
        val result = validateQuantity("5")
        assertNull(result)
    }
}