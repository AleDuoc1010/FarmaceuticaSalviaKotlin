package com.example.farmaceuticasalvia.ui.viewmodel

import com.example.farmaceuticasalvia.data.remote.dto.ItemPedidoDto
import com.example.farmaceuticasalvia.data.remote.dto.PedidoResponse
import com.example.farmaceuticasalvia.data.remote.dto.ProductoResponse
import com.example.farmaceuticasalvia.data.repository.CartRepository
import com.example.farmaceuticasalvia.data.repository.ProductRepository
import com.example.farmaceuticasalvia.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val cartRepo = mockk<CartRepository>()
    private val productRepo = mockk<ProductRepository>()

    private lateinit var viewModel: CartViewModel

    @Test
    fun loadCart_exitoso_combina_datos_del_carrito_con_datos_del_producto() = runTest {


        val itemPedido = ItemPedidoDto(id = 1, sku = "SKU-1", cantidad = 2, precioUnitario = 1000.0, subtotal = 2000.0)
        val mockPedido = PedidoResponse(id = 100, uuid = "uuid-ped", montoTotal = 2000.0, estado = "PENDIENTE", items = listOf(itemPedido))

        val mockProducto = ProductoResponse(id = 1, sku = "SKU-1", nombre = "Paracetamol", descripcion = "Desc", precio = 1000.0, imagenUrl = "img.jpg", destacado = false, pideReceta = false)

        coEvery { cartRepo.getCart() } returns Result.success(mockPedido)
        coEvery { productRepo.getProductBySku("SKU-1") } returns Result.success(mockProducto)

        viewModel = CartViewModel(cartRepo, productRepo)

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)

        assertEquals(1, state.items.size)

        val itemUi = state.items[0]
        assertEquals("Paracetamol", itemUi.name)
        assertEquals(2, itemUi.quantity)
        assertEquals(2000.0, state.total, 0.0)
    }

    @Test
    fun checkout_exitoso_cambia_estado_a_checkoutSuccess() = runTest {
        coEvery { cartRepo.getCart() } returns Result.success(null)

        viewModel = CartViewModel(cartRepo, productRepo)

        val mockPedidoPagado = PedidoResponse(1, "uuid", 0.0, "PAGADO", emptyList())
        coEvery { cartRepo.checkout() } returns Result.success(mockPedidoPagado)

        viewModel.checkout()

        assertTrue(viewModel.uiState.value.checkoutSuccess)

        assertEquals(0, viewModel.uiState.value.items.size)
    }

    @Test
    fun loadCart_maneja_error_del_servidor() = runTest {
        coEvery { cartRepo.getCart() } returns Result.failure(Exception("Error 500"))

        viewModel = CartViewModel(cartRepo, productRepo)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMsg)
        assertTrue(state.errorMsg!!.contains("Error 500"))
    }
}