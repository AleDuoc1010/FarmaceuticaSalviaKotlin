package com.example.farmaceuticasalvia.data.remote.api

import com.example.farmaceuticasalvia.data.remote.dto.PageResponse
import com.example.farmaceuticasalvia.data.remote.dto.ProductoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatalogoApiService {

    @GET("productos")
    suspend fun getAllProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<ProductoResponse>>

    @GET("productos/destacados")
    suspend fun getDestacados(
        @Query("page") page: Int = 0
    ): Response<PageResponse<ProductoResponse>>

    @GET("productos/{sku}")
    suspend fun getProductosBySku(@Path("sku") sku: String): Response<ProductoResponse>
}