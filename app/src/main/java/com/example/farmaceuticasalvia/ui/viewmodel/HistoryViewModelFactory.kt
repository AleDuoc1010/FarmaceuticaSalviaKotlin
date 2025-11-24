package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.farmaceuticasalvia.data.repository.HistoryRepository
import com.example.farmaceuticasalvia.data.repository.ProductRepository

class HistoryViewModelFactory(
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HistoryViewModel::class.java)){
            return HistoryViewModel(historyRepository, productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}