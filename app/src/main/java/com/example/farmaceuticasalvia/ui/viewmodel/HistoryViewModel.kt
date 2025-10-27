package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmaceuticasalvia.data.local.history.PurchaseRecord
import com.example.farmaceuticasalvia.data.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val historyRepository: HistoryRepository
): ViewModel() {

    val history: StateFlow<List<PurchaseRecord>> = historyRepository.purchaseHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun removeFromHistory(historyItemId: Long) {
        viewModelScope.launch {
            historyRepository.removeFromHistory(historyItemId)
        }
    }

    fun clearHistory(){
        viewModelScope.launch {
            historyRepository.clearHistory()
        }
    }
}