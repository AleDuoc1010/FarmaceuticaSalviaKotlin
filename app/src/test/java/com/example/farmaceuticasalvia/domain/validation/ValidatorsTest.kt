package com.example.farmaceuticasalvia.domain.validation

import org.junit.Assert.*
import org.junit.Test

class ValidatorsTest {

    @Test
    fun `validateName retorna error si esta vacio`() {
        assertEquals("El nombre es obligatorio", validateName(""))
    }

    @Test
    fun `validateName retorna error si tiene numeros`() {
        assertEquals("Formato incorrecto", validateName("Juan123"))
    }

    @Test
    fun `validateName retorna null si es valido`() {
        assertNull(validateName("Juan Pérez"))
    }

    @Test
    fun `validatePhone retorna error si es corto`() {
        assertEquals("El telefono debe tener 9 digitos", validatePhone("12345"))
    }

    @Test
    fun `validatePhone retorna error si tiene letras`() {
        assertEquals("Solo numeros", validatePhone("12345abc"))
    }

    @Test
    fun `validatePhone retorna null si es valido`() {
        assertNull(validatePhone("912345678"))
    }

    @Test
    fun `validatePassword detecta longitud corta`() {
        assertEquals("La contraseña debe tener un minimo de 6 caracteres", validatePassword("123"))
    }

    @Test
    fun `validatePassword requiere mayuscula`() {
        assertEquals("La contraseña debe tener mayusculas", validatePassword("admin123!"))
    }

    @Test
    fun `validatePassword requiere minuscula`() {
        assertEquals("La contraseña debe tener minusculas", validatePassword("ADMIN123!"))
    }

    @Test
    fun `validatePassword requiere numero`() {
        assertEquals("Debe incluir numeros", validatePassword("Admin!"))
    }

    @Test
    fun `validatePassword requiere simbolo`() {
        assertEquals("La contraseña debe tener al menos un simbolo", validatePassword("Admin123"))
    }

    @Test
    fun `validatePassword no permite espacios`() {
        assertEquals("No debe contener espacios", validatePassword("Admin 123!"))
    }

    @Test
    fun `validatePassword valido retorna null`() {
        assertNull(validatePassword("Admin123!"))
    }

    @Test
    fun `validateConfirm detecta diferencias`() {
        assertEquals("Las contraseñas no son iguales", validateConfirm("123", "124"))
    }

    @Test
    fun `validateConfirm valido retorna null`() {
        assertNull(validateConfirm("123", "123"))
    }
}