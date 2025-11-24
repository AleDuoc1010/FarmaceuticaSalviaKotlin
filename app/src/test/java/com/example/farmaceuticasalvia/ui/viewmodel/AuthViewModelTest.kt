package com.example.farmaceuticasalvia.ui.viewmodel

import com.example.farmaceuticasalvia.data.remote.dto.UsuarioDto
import com.example.farmaceuticasalvia.data.repository.UserRepository
import com.example.farmaceuticasalvia.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Solución "Cannot infer type": Especificamos <UserRepository>
    private val repository = mockk<UserRepository>()
    private lateinit var viewModel: AuthViewModel

    @Test
    fun onLoginEmailChange_actualiza_estado_y_valida_email_incorrecto() {
        // ARRANGE
        viewModel = AuthViewModel(repository)

        // ACT
        viewModel.onLoginEmailChange("correo-invalido")

        // ASSERT
        val state = viewModel.login.value
        assertEquals("correo-invalido", state.email)
        assertEquals("Formato invalido", state.emailError)
        assertFalse(state.canSubmit)
    }

    @Test
    fun onLoginEmailChange_actualiza_estado_y_valida_email_correcto() {
        // ARRANGE
        viewModel = AuthViewModel(repository)

        // ACT
        viewModel.onLoginEmailChange("test@gmail.com")

        // ASSERT
        val state = viewModel.login.value
        assertNull(state.emailError)
    }

    @Test
    fun submitLogin_exitoso_actualiza_success_a_true() = runTest {
        // ARRANGE
        viewModel = AuthViewModel(repository)
        viewModel.onLoginEmailChange("test@gmail.com")
        viewModel.onLoginPassChange("Password123!")

        val mockUser = UsuarioDto("uuid", "Test", "test@gmail.com", "USER")

        // Solución "Unresolved reference coEvery": Importar io.mockk.coEvery
        // Solución "Unresolved reference any": Importar io.mockk.any
        coEvery { repository.login(any(), any()) } returns Result.success(mockUser)

        // ACT
        viewModel.submitLogin()

        // ASSERT
        val state = viewModel.login.value
        assertTrue(state.success)
        assertFalse(state.isSubmitting)
        assertNull(state.errorMsg)
    }

    @Test
    fun submitLogin_fallido_muestra_mensaje_de_error() = runTest {
        // ARRANGE
        viewModel = AuthViewModel(repository)
        viewModel.onLoginEmailChange("test@gmail.com")
        viewModel.onLoginPassChange("Password123!")

        coEvery { repository.login(any(), any()) } returns Result.failure(Exception("Credenciales malas"))

        // ACT
        viewModel.submitLogin()

        // ASSERT
        val state = viewModel.login.value
        assertFalse(state.success)
        assertFalse(state.isSubmitting)
        assertEquals("Credenciales malas", state.errorMsg)
    }
}