package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmaceuticasalvia.data.repository.HistoryRepository
import com.example.farmaceuticasalvia.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryItemUi(
    val sku: String,
    val name: String,
    val imageUrl: String,
    val quantity: Int,
    val price: Double
)

data class HistoryOrderUi(
    val id: Long,
    val uuid: String,
    val total: Double,
    val status: String,
    val items: List<HistoryItemUi>
)

data class HistoryUiState(
    val orders: List<HistoryOrderUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)

class HistoryViewModel(
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            val result = historyRepository.getHistory()

            if (result.isSuccess){
                val pedidosDto = result.getOrNull() ?: emptyList()

                val enrichedOrders = pedidosDto.map { pedido ->

                    val enrichedItems = pedido.items.map { itemDto ->
                        val productResult = productRepository.getProductBySku(itemDto.sku)
                        val productInfo = productResult.getOrNull()

                        HistoryItemUi(
                            sku = itemDto.sku,
                            name = productInfo?.nombre ?: itemDto.sku,
                            imageUrl = productInfo?.imagenUrl ?: "",
                            quantity = itemDto.cantidad,
                            price = itemDto.precioUnitario
                        )
                    }

                    HistoryOrderUi(
                        id = pedido.id,
                        uuid = pedido.uuid,
                        total = pedido.montoTotal,
                        status = pedido.estado,
                        items = enrichedItems
                    )
                }

                _uiState.update {
                    it.copy(orders = enrichedOrders, isLoading = false)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = "Error al cargar historial: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun removeFromHistory(historyItemId: Long){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = historyRepository.removeFromHistory(historyItemId)

            if (result.isSuccess){
                loadHistory()
            } else{
                _uiState.update {
                    it.copy(isLoading = false, errorMsg = "No se pudo eliminar el registro")
                }
            }
        }
    }

    fun clearHistory(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = historyRepository.clearHistory()

            if (result.isSuccess){
                _uiState.update { it.copy(orders = emptyList(), isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMsg = "No se pudo vaciar el historial")
                }
            }
        }
    }
}