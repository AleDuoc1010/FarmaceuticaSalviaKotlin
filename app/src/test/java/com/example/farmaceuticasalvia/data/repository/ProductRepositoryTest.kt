package com.example.farmaceuticasalvia.data.repository

import com.example.farmaceuticasalvia.data.remote.api.CatalogoApiService
import com.example.farmaceuticasalvia.data.remote.dto.PageResponse
import com.example.farmaceuticasalvia.data.remote.dto.ProductoResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ProductRepositoryTest {

    private val api = mockk<CatalogoApiService>()
    private val repository = ProductRepository(api)

    @Test
    fun `getAllProducts retorna lista cuando API responde 200`() = runBlocking {

        val mockProducts = listOf(
            ProductoResponse(1, "SKU1", "Prod 1", "Desc", 100.0, "url", false, false),
            ProductoResponse(2, "SKU2", "Prod 2", "Desc", 200.0, "url", true, true)
        )

        val mockPage = PageResponse(mockProducts, 1, 2, true)

        coEvery { api.getAllProducts(any(), any()) } returns Response.success(mockPage)

        val result = repository.getAllProducts()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Prod 1", result.getOrNull()?.get(0)?.nombre)
    }

    @Test
    fun `getProductBySku retorna error si API responde 404`() = runBlocking {

        val errorBody = "".toResponseBody(null)
        coEvery { api.getProductosBySku("SKU-NO-EXISTE") } returns Response.error(404, errorBody)

        val result = repository.getProductBySku("SKU-NO-EXISTE")

        assertTrue(result.isFailure)
        assertEquals("Producto no encontrado", result.exceptionOrNull()?.message)
    }
}