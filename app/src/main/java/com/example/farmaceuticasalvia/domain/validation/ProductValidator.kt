package com.example.farmaceuticasalvia.domain.validation

import android.util.Patterns

fun validateQuantity(quantity: String): String? {
    if(quantity.isBlank()) return "la cantidad es obligatoria"

    if(!quantity.all { it.isDigit() }) return "Solo números"

    val num = quantity.toIntOrNull()

    if(num == null) return "Formato invalido"

    if(num < 1) return "La cantidad no puede ser menor a 1"

    return null
}

fun validatePhone_Product(phone: String): String? {
    if (phone.isBlank()) return "El telefono es obligatorio"
    if (!phone.all { it.isDigit() }) return "Solo numeros"
    if (phone.length < 9) return "El telefono debe tener 9 digitos"
    return null
}