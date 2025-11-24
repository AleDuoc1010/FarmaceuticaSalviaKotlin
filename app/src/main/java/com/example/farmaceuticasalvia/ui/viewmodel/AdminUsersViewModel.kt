package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.farmaceuticasalvia.data.local.storage.UserPreferences
import com.example.farmaceuticasalvia.data.remote.dto.UsuarioDto
import com.example.farmaceuticasalvia.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUsersUiState(
    val users: List<UsuarioDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val currentAdminUuid: String = ""
)

class AdminUsersViewModel(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
        getCurrentUserUuid()
    }

    private fun getCurrentUserUuid() {
        viewModelScope.launch {
            val uuid = userPreferences.userUuid.first() ?: ""
            _uiState.update { it.copy(currentAdminUuid = uuid) }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            val result = repository.getAllUsers()

            if (result.isSuccess) {
                _uiState.update { it.copy(users = result.getOrNull() ?: emptyList(), isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMsg = "Error: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun deleteUser(uuid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = repository.deleteUser(uuid)

            if (result.isSuccess) {
                loadUsers()
            } else {
                _uiState.update { it.copy(isLoading = false, errorMsg = "No se pudo eliminar") }
            }
        }
    }
}

