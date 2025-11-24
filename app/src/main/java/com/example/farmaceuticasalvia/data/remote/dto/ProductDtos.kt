package com.example.farmaceuticasalvia.data.remote.dto

data class ProductoResponse(
    val id: Long,
    val sku: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val destacado: Boolean,
    val pideReceta: Boolean
)

data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val last: Boolean
)