package com.example.farmaceuticasalvia.domain.validation


fun validateQuantity(quantity: String): String? {
    if(quantity.isBlank()) return "la cantidad es obligatoria"

    if(!quantity.all { it.isDigit() }) return "Solo números"

    val num = quantity.toIntOrNull()

    if(num == null) return "Formato invalido"

    if(num < 1) return "La cantidad no puede ser menor a 1"

    return null
}