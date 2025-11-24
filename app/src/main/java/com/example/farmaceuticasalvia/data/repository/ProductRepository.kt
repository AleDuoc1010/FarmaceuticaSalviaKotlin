package com.example.farmaceuticasalvia.data.repository

import com.example.farmaceuticasalvia.data.remote.api.CatalogoApiService
import com.example.farmaceuticasalvia.data.remote.dto.ProductoResponse
import okio.IOException

class ProductRepository(
    private val api: CatalogoApiService
) {

    suspend fun getAllProducts(): Result<List<ProductoResponse>>{
        return try {
            val response = api.getAllProducts(page = 0, size = 100)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.content)
            } else {
                Result.failure(Exception("Error al cargar productos: ${response.code()}"))
            }
        } catch (e: IOException){
            Result.failure(Exception("No hay conexión a internet"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun GetFeaturedProducts(): Result<List<ProductoResponse>>{
        return try {
            val response = api.getDestacados()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.content)
            } else {
                Result.failure(Exception("Error al cargar destacados"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductBySku(sku: String): Result<ProductoResponse>{
        return try {
            val response = api.getProductosBySku(sku)

            if (response.isSuccessful && response.body() != null){
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Producto no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}