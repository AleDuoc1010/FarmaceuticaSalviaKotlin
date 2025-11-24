package com.example.farmaceuticasalvia.data.remote.dto

data class  RegisterRequest(
    val nombre: String,
    val email: String,
    val phone: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val usuario: UsuarioDto
)

data class UsuarioDto(
    val uuid: String,
    val nombre: String,
    val email: String,
    val rol: String
)