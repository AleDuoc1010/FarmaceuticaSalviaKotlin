package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.farmaceuticasalvia.data.repository.CartRepository
import com.example.farmaceuticasalvia.data.repository.ProductRepository

class CartViewModelFactory(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
): ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CartViewModel::class.java)){
            return CartViewModel(cartRepository, productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}