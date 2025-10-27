package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.farmaceuticasalvia.data.repository.CartRepository
import com.example.farmaceuticasalvia.data.repository.HistoryRepository
import com.example.farmaceuticasalvia.data.repository.ProductRepository

class ProductViewModelFactory(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ProductViewModel::class.java)){
            return ProductViewModel(repository, cartRepository, historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}