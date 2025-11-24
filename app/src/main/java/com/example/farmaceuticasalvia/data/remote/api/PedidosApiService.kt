package com.example.farmaceuticasalvia.data.remote.api

import com.example.farmaceuticasalvia.data.remote.dto.AgregarItemRequest
import com.example.farmaceuticasalvia.data.remote.dto.PedidoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PedidosApiService {

    @GET("pedidos/carrito")
    suspend fun  getCarrito(): Response<PedidoResponse>

    @POST("pedidos/carrito")
    suspend fun agregarAlCarrito(@Body request: AgregarItemRequest): Response<PedidoResponse>

    @POST("pedidos/carrito/pagar")
    suspend fun  pagarCarrito(): Response<PedidoResponse>

    @DELETE("pedidos/carrito/{sku}")
    suspend fun eliminarItemCarrito(@Path("sku") sku: String): Response<Void>

    @DELETE("pedidos/carrito")
    suspend fun vaciarCarrito(): Response<Void>

    @POST("pedidos/comprar")
    suspend fun comprarDirecto(@Body request: AgregarItemRequest): Response<PedidoResponse>

    @GET("pedidos/historial")
    suspend fun getHistorial(): Response<List<PedidoResponse>>

    @DELETE("pedidos/historial/{id}")
    suspend fun deleteHistoryItem(@Path("id") id: Long): Response<Void>

    @DELETE("pedidos/historial")
    suspend fun clearHistory(): Response<Void>
}