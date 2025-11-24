package com.example.farmaceuticasalvia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmaceuticasalvia.data.repository.UserRepository
import com.example.farmaceuticasalvia.domain.validation.validateConfirm
import com.example.farmaceuticasalvia.domain.validation.validateEmail
import com.example.farmaceuticasalvia.domain.validation.validateName
import com.example.farmaceuticasalvia.domain.validation.validatePassword
import com.example.farmaceuticasalvia.domain.validation.validatePhone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class RegisterUiState(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val pass: String = "",
    val confirm: String = "",

    val nameError: String? = null,
    val phoneError: String? = null,
    val emailError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

class AuthViewModel(
    private val repository: UserRepository
): ViewModel(){

    private val _login = MutableStateFlow(LoginUiState())
    val login : StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register : StateFlow<RegisterUiState> = _register


    fun onLoginEmailChange(value: String){
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String){
        _login.update { it.copy(pass = value, passError = validatePassword(value)) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit(){
        val s = _login.value
        val can = s.emailError == null &&
                s.email.isNotBlank() &&
                s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin(){
        val s = _login.value

        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {

            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val result = repository.login(s.email.trim(), s.pass)

            _login.update {
                if (result.isSuccess) {
                    it.copy(
                        isSubmitting = false,
                        success = true,
                        errorMsg = null
                    )
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación"
                    )
                }
            }
        }
    }

    fun clearLoginResult(){
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    private fun recomputeRegisterCanSubmit(){
        val s = _register.value
        val noErrors = listOf(s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError).all { it == null }
        val filled = s.name.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun onNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(name = filtered, nameError = validateName(filtered))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        val finalPhone = if (digitsOnly.length > 9) digitsOnly.take(9) else digitsOnly
        _register.update {
            it.copy(phone = finalPhone, phoneError = validatePhone(finalPhone))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String){
        _register.update { it.copy(pass = value, passError = validatePassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm))}
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String){
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) }
        recomputeRegisterCanSubmit()
    }

    fun submitRegister(){
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val result = repository.register(
                name = s.name.trim(),
                email = s.email.trim(),
                phone = s.phone.trim(),
                password = s.pass
            )

            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(isSubmitting = false, success = false, errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar")
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update {it.copy(success = false, errorMsg = null)}
    }
}