package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.farmaceuticasalvia.data.local.products.ProductEntity
import com.example.farmaceuticasalvia.data.repository.CartRepository
import com.example.farmaceuticasalvia.data.repository.HistoryRepository
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
    val photoUriString: String? = null
)
class ProductViewModel(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository
): ViewModel(){

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products: StateFlow<List<ProductEntity>> = _products

    private val _recipeError = MutableStateFlow<String?>(null)
    val recipeError: StateFlow<String?> = _recipeError.asStateFlow()

    private val _featuredProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val featuredProducts: StateFlow<List<ProductEntity>> = _featuredProducts

    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

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

    init {
        fetchProducts()
        fetchFeaturedProducts()
    }

    private fun fetchProducts(){
        viewModelScope.launch {
            repository.getAllProducts().collect { list ->
                _products.value = list
            }
        }
    }

    private fun fetchFeaturedProducts(){
        viewModelScope.launch {
            repository.GetFeaturedProducts().collect { list ->
            _featuredProducts.value = list}
        }
    }

    fun onProductSelected(product: ProductEntity, type: ActiveModal){
        _selectedProduct.value = product
        _activeModal.value = type
    }

    fun onModalDismiss(){
        _selectedProduct.value = null
        _activeModal.value = null
        _modalUiState.value = ProductUiState()
        _quantityError.value = null
        _phoneError.value = null
        _recipeError.value = null
    }

    fun onQuantityChanged(quantity: String){
        _modalUiState.update { it.copy(quantity = quantity) }
        _quantityError.value = validateQuantity(quantity)
    }

    fun onPhoneChanged(phone: String){
        _modalUiState.update { it.copy(phone = phone) }
        _phoneError.value = validatePhone(phone)
    }

    fun onConfirmBuy(){
        val quantityStr = _modalUiState.value.quantity
        val phoneStr = _modalUiState.value.phone
        val state = _modalUiState.value
        val product = _selectedProduct.value

        val quantityError = validateQuantity(quantityStr)
        val phoneError = validatePhone(phoneStr)

        var recipeError : String? = null
        if (product != null && product.requireRecipe && state.photoUriString == null){
            recipeError = "Este producto requiere una foto de la receta"
        }

        _quantityError.value = quantityError
        _phoneError.value = phoneError
        _recipeError.value = recipeError

        if(quantityError != null || phoneError != null || recipeError != null){
            return
        }

        val quantityInt = quantityStr.toInt()

        if(product != null){
            viewModelScope.launch {
                historyRepository.addToHistory(product, quantityInt, phoneStr)
            }
        }

        val productName = product?.name ?: "Producto"
        viewModelScope.launch {
            _showPurchaseNotificationEvent.emit(productName)
        }

        onModalDismiss()
    }

    fun onConfirmCart(){
        val quantityStr = _modalUiState.value.quantity
        val quantityError = validateQuantity(quantityStr)

        _quantityError.value = quantityError

        if(quantityError != null){
            return
        }

        val product = _selectedProduct.value
        val quantityInt = quantityStr.toInt()

        if(product != null) {
            viewModelScope.launch {
                cartRepository.addToCart(product, quantityInt)
            }
        }

        onModalDismiss()
    }

    fun onPhotoTaken(uri: String) {
        _modalUiState.update { it.copy(photoUriString = uri) }
        _recipeError.value = null
    }

    fun onDeletePhoto(){
        _modalUiState.update { it.copy(photoUriString = null) }
    }
}