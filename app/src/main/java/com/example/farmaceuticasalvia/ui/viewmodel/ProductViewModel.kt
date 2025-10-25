package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmaceuticasalvia.data.local.products.ProductEntity
import com.example.farmaceuticasalvia.data.repository.ProductRepository
import com.example.farmaceuticasalvia.domain.validation.validatePhone
import com.example.farmaceuticasalvia.domain.validation.validateQuantity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ActiveModal{
    BUY, CART
}

data class ProductUiState(
    val quantity: String = "1",
    val phone: String = ""
)
class ProductViewModel(
    private val repository: ProductRepository
): ViewModel(){

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products: StateFlow<List<ProductEntity>> = _products

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

    init {
        fetchProducts()
    }

    private fun fetchProducts(){
        viewModelScope.launch {
            val list = repository.getAllProducts()
            _products.value = list
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

        val quantityError = validateQuantity(quantityStr)
        val phoneError = validatePhone(phoneStr)

        _quantityError.value = quantityError
        _phoneError.value = phoneError

        if(quantityError != null || phoneError != null){
            return
        }

        val product = _selectedProduct.value
        val quantityInt = quantityStr.toInt()

        print("Compra Exitosa")
        print("Producto: ${product?.name}")
        print("Cantidad: ${quantityInt}")
        print("Telefono: ${phoneStr}")

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

        print("Añadadido al carrito")
        print("Producto: ${product?.name}")
        print("Cantidad: ${quantityInt}")

        onModalDismiss()
    }
}