package com.example.farmaceuticasalvia.data.remote.dto

data class AgregarItemRequest(
    val sku: String,
    val cantidad: Int
)

data class PedidoResponse(
    val id: Long,
    val uuid: String,
    val montoTotal: Double,
    val estado: String,
    val items: List<ItemPedidoDto>
)

data class ItemPedidoDto(
    val id: Long,
    val sku: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)