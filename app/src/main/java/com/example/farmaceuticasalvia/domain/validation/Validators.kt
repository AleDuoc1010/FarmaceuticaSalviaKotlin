package com.example.farmaceuticasalvia.domain.validation

import android.util.Patterns

fun validateEmail(email: String): String?{
    if(email.isBlank()) return "El email es obligatorio"
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if (!ok) "Formato invalido" else null
}

fun validateName(name: String): String? {
    if (name.isBlank()) return "El nombre es obligatorio"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(name)) "Formato incorrecto" else null
}

fun validatePhone(phone: String): String? {
    if (phone.isBlank()) return "El telefono es obligatorio"
    if (!phone.all { it.isDigit() }) return "Solo numeros"
    if (phone.length < 9) return "El telefono debe tener 9 digitos"
    return null
}

fun validatePassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria"
    if (pass.length < 6) return "La contraseña debe tener un minimo de 6 caracteres"
    if (!pass.any{it.isUpperCase()}) return "La contraseña debe tener mayusculas"
    if (!pass.any{it.isLowerCase()}) return "La contraseña debe tener minusculas"
    if (!pass.any{it.isDigit()}) return "Debe incluir numeros"
    if (!pass.any{!it.isLetterOrDigit()}) return "La contraseña debe tener al menos un simbolo"
    if (pass.contains(' ')) return "No debe contener espacios"
    return null
}

fun validateConfirm(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Confirma tu contraseña"
    return if (pass != confirm) "Las contraseñas no son iguales" else null
}