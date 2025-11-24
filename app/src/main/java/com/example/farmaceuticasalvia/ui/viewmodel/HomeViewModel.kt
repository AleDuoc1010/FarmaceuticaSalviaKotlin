package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmaceuticasalvia.data.repository.ExternalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val dolarValue: Double? = null,
    val ufValue: Double? = null,
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)

class HomeViewModel(
    private val externalRepository: ExternalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchIndicadores()
    }

    private fun fetchIndicadores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = externalRepository.getIndicadores()

            result.onSuccess { data ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dolarValue = data.dolar.valor,
                        ufValue = data.uf.valor,
                        errorMsg = null
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = "No se pudo cargar indicadores"
                    )
                }
            }
        }
    }
}