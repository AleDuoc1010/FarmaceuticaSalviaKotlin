package com.example.farmaceuticasalvia.domain.validation

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmailValidatorTest {

    @Test
    fun `validateEmail retorna error si esta vacio`() {
        val result = validateEmail("")
        assertEquals("El email es obligatorio", result)
    }

    @Test
    fun `validateEmail retorna error si formato es invalido`() {
        val result = validateEmail("correo-invalido")
        assertEquals("Formato invalido", result)
    }

    @Test
    fun `validateEmail retorna null si es valido`() {
        val result = validateEmail("test@ejemplo.com")
        assertNull(result)
    }
}