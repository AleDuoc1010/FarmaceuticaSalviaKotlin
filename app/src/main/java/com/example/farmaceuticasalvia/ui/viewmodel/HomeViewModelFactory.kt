package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.farmaceuticasalvia.data.repository.ExternalRepository

class HomeViewModelFactory(
    private val externalRepository: ExternalRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(externalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}