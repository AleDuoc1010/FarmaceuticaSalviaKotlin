package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmaceuticasalvia.data.repository.CartRepository
import com.example.farmaceuticasalvia.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartItemUi(
    val id: Long,
    val sku: String,
    val name: String,
    val imageUrl: String,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)

data class CartUiState(
    val items: List<CartItemUi> = emptyList(),
    val total: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val checkoutSuccess: Boolean = false
)

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
): ViewModel(){

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            val result = cartRepository.getCart()

            if (result.isSuccess) {
                val pedido = result.getOrNull()

                if (pedido == null){
                    _uiState.update { it.copy(items = emptyList(), total = 0.0, isLoading = false) }
                } else {
                    val enrichedItems = pedido.items.map { itemDto ->
                        val productResult = productRepository.getProductBySku(itemDto.sku)
                        val productInfo = productResult.getOrNull()

                        CartItemUi(
                            id = itemDto.id,
                            sku = itemDto.sku,
                            name = productInfo?.nombre ?: "Producto desconocido",
                            imageUrl = productInfo?.imagenUrl ?: "",
                            quantity = itemDto.cantidad,
                            price = itemDto.precioUnitario,
                            subtotal = itemDto.subtotal
                        )
                    }
                    _uiState.update {
                        it.copy(
                            items = enrichedItems,
                            total = pedido.montoTotal,
                            isLoading = false
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMsg = "Error cargando carrito: ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }
    fun removeFromCart(sku: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = cartRepository.removeFromCart(sku)

            if (result.isSuccess) {
                loadCart()
            } else {
                _uiState.update { it.copy(isLoading = false, errorMsg = "No se pudo eliminar") }
            }
        }
    }
    fun clearCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = cartRepository.clearCart()
            if (result.isSuccess) {
                _uiState.update { it.copy(items = emptyList(), total = 0.0, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMsg = "Error al vaciar carrito") }
            }
        }
    }

    fun checkout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = cartRepository.checkout()

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        checkoutSuccess = true,
                        items = emptyList(),
                        total = 0.0
                    )
                }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error en el pago"
                _uiState.update { it.copy(isLoading = false, errorMsg = error) }
            }
        }
    }

    fun onQuantityChanged(sku: String, currentQuantity: Int, delta: Int) {
        val newQuantity = currentQuantity + delta

        if (newQuantity < 1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = cartRepository.updateQuantity(sku, newQuantity)

            if (result.isSuccess) {
                loadCart()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Error al actualizar"

                _uiState.update { it.copy(isLoading = false, errorMsg = error) }
            }
        }
    }

    fun onCheckoutSuccessHandled() {
        _uiState.update { it.copy(checkoutSuccess = false) }
    }
}

