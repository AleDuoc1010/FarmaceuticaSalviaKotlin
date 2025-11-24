package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmaceuticasalvia.data.remote.dto.ProductoResponse
import com.example.farmaceuticasalvia.data.repository.CartRepository
import com.example.farmaceuticasalvia.data.repository.ProductRepository
import com.example.farmaceuticasalvia.domain.validation.validatePhone
import com.example.farmaceuticasalvia.domain.validation.validateQuantity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ActiveModal{
    BUY, CART
}

data class ProductUiState(
    val quantity: String = "1",
    val phone: String = "",
    val photoUriString: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProductViewModel(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository,
): ViewModel(){

    private val _products = MutableStateFlow<List<ProductoResponse>>(emptyList())
    val products: StateFlow<List<ProductoResponse>> = _products

    private val _recipeError = MutableStateFlow<String?>(null)
    val recipeError: StateFlow<String?> = _recipeError.asStateFlow()

    private val _featuredProducts = MutableStateFlow<List<ProductoResponse>>(emptyList())
    val featuredProducts: StateFlow<List<ProductoResponse>> = _featuredProducts

    private val _selectedProduct = MutableStateFlow<ProductoResponse?>(null)
    val selectedProduct: StateFlow<ProductoResponse?> = _selectedProduct.asStateFlow()

    private val _activeModal = MutableStateFlow<ActiveModal?>(null)
    val activeModal: StateFlow<ActiveModal?> = _activeModal.asStateFlow()

    private val _modalUiState = MutableStateFlow(ProductUiState())
    val modalUiState: StateFlow<ProductUiState> = _modalUiState.asStateFlow()

    private val _quantityError = MutableStateFlow<String?>(null)
    val quantityError: StateFlow<String?> = _quantityError.asStateFlow()

    private val _phoneError = MutableStateFlow<String?>(null)
    val phoneError: StateFlow<String?> = _phoneError.asStateFlow()

    private val _showPurchaseNotificationEvent = MutableSharedFlow<String>()
    val showPurchaseNotificationEvent = _showPurchaseNotificationEvent.asSharedFlow()

    private val _showErrorEvent = MutableSharedFlow<String>()

    val showErrorEvent = _showErrorEvent.asSharedFlow()

    init {
        fetchProducts()
        fetchFeaturedProducts()
    }

    init {
        refreshData()
    }

    fun refreshData() {
        fetchProducts()
        fetchFeaturedProducts()
    }

    private fun fetchProducts(){
        viewModelScope.launch {
            val result = repository.getAllProducts()
            result.onSuccess { list ->
                _products.value = list
            }
        }
    }

    private fun fetchFeaturedProducts(){
        viewModelScope.launch {
            val result = repository.GetFeaturedProducts()
            result.onSuccess { list ->
                _featuredProducts.value = list
            }
        }
    }

    fun onProductSelected(product: ProductoResponse, type: ActiveModal){
        _selectedProduct.value = product
        _activeModal.value = type
        _modalUiState.value = ProductUiState()
        _quantityError.value = null
        _phoneError.value = null
        _recipeError.value = null
    }

    fun onModalDismiss(){
        _selectedProduct.value = null
        _activeModal.value = null
    }

    fun onQuantityChanged(quantity: String){
        _modalUiState.update { it.copy(quantity = quantity) }
        _quantityError.value = validateQuantity(quantity)
    }

    fun onPhoneChanged(phone: String){
        _modalUiState.update { it.copy(phone = phone) }
        _phoneError.value = validatePhone(phone)
    }

    fun onPhotoTaken(uri: String) {
        _modalUiState.update { it.copy(photoUriString = uri) }
        _recipeError.value = null
    }

    fun onDeletePhoto(){
        _modalUiState.update { it.copy(photoUriString = null) }
    }

    fun onConfirmBuy(){
        val quantityStr = _modalUiState.value.quantity
        val phoneStr = _modalUiState.value.phone
        val state = _modalUiState.value
        val product = _selectedProduct.value ?: return

        val quantityError = validateQuantity(quantityStr)
        val phoneError = validatePhone(phoneStr)

        var recipeError : String? = null

        if (product.pideReceta && state.photoUriString == null){
            recipeError = "Este producto requiere una foto de la receta"
        }

        _quantityError.value = quantityError
        _phoneError.value = phoneError
        _recipeError.value = recipeError

        if(quantityError != null || phoneError != null || recipeError != null) return

        val quantityInt = quantityStr.toInt()

        viewModelScope.launch {
            _modalUiState.update { it.copy(isLoading = true) }

            val result = cartRepository.comprarDirecto(product.sku, quantityInt)

            _modalUiState.update { it.copy(isLoading = false) }

            result.onSuccess {
                _showPurchaseNotificationEvent.emit("Compra exitosa: ${product.nombre}")
                onModalDismiss()
            }.onFailure { e ->
                _showErrorEvent.emit(e.message ?: "Error al comprar")
            }
        }
    }

    fun onConfirmCart() {
        val quantityStr = _modalUiState.value.quantity
        val state = _modalUiState.value
        val product = _selectedProduct.value ?: return

        val quantityError = validateQuantity(quantityStr)

        var recipeError: String? = null
        if (product.pideReceta && state.photoUriString == null) {
            recipeError = "Este producto requiere una foto de la receta"
        }

        _quantityError.value = quantityError
        _recipeError.value = recipeError

        if (quantityError != null || recipeError != null) return

        val quantityInt = quantityStr.toInt()

        viewModelScope.launch {
            _modalUiState.update { it.copy(isLoading = true) }

            val result = cartRepository.addToCart(product.sku, quantityInt)

            _modalUiState.update { it.copy(isLoading = false) }

            result.onSuccess {
                _showPurchaseNotificationEvent.emit("Agregado al carrito: ${product.nombre}")
                onModalDismiss()
            }.onFailure { e ->
                _showErrorEvent.emit(e.message ?: "Error al agregar")
            }
        }
    }
}