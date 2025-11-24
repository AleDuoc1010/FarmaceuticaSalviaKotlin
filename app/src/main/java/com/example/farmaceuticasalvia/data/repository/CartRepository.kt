package com.example.farmaceuticasalvia.data.repository

import com.example.farmaceuticasalvia.data.remote.api.PedidosApiService
import com.example.farmaceuticasalvia.data.remote.dto.AgregarItemRequest
import com.example.farmaceuticasalvia.data.remote.dto.PedidoResponse
import okio.IOException

class CartRepository(
    private val api: PedidosApiService
){

    suspend fun getCart(): Result<PedidoResponse?> {
        return try {
            val response = api.getCarrito()

            if (response.isSuccessful){
                if (response.code() == 204){
                    Result.success(null)
                } else {
                    Result.success(response.body())
                }
            } else {
                Result.failure(Exception("Error al cargar el carrito: ${response.code()}"))
            }
        } catch (e: IOException){
            Result.failure(Exception("Sin conexión a internet"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun addToCart(sku: String, quantity: Int): Result<PedidoResponse>{
        return try {
            val request = AgregarItemRequest(sku, quantity)
            val response = api.agregarAlCarrito(request)

            if (response.isSuccessful && response.body() != null){
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al agregar al carrito"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromCart(sku: String): Result<Unit>{
        return try {
            val response = api.eliminarItemCarrito(sku)
            if (response. isSuccessful){
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar item"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun clearCart(): Result<Unit> {
        return try {
            val response = api.vaciarCarrito()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al vaciar carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkout(): Result<PedidoResponse> {
        return try {
            val response = api.pagarCarrito()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al procesar el pago"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun comprarDirecto(sku: String, quantity: Int): Result<PedidoResponse> {
        return try {
            val request = AgregarItemRequest(sku, quantity)
            val response = api.comprarDirecto(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al realizar la compra"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}