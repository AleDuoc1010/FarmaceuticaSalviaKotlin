package com.example.farmaceuticasalvia.data.repository

import com.example.farmaceuticasalvia.data.local.storage.UserPreferences
import com.example.farmaceuticasalvia.data.remote.api.UsuariosApiService
import com.example.farmaceuticasalvia.data.remote.dto.LoginResponse
import com.example.farmaceuticasalvia.data.remote.dto.UsuarioDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class UserRepositoryTest {

    private val api = mockk<UsuariosApiService>()
    private val preferences = mockk<UserPreferences>(relaxed = true)


    private val repository = UserRepository(api, preferences)

    @Test
    fun `login retorna Success cuando la API responde 200`() = runBlocking {

        val email = "test@gmail.com"
        val pass = "123456"
        val mockUser = UsuarioDto("uuid-1", "Test", email, "USUARIO")
        val mockResponse = LoginResponse("token-falso", mockUser)

        coEvery { api.login(any()) } returns Response.success(mockResponse)

        val result = repository.login(email, pass)

        assertTrue(result.isSuccess)
        assertEquals("Test", result.getOrNull()?.nombre)

        coVerify { preferences.saveUserSession(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `login retorna Failure cuando la API responde 401`() = runBlocking {

        val errorBody = "Credenciales invalidas".toResponseBody(null)
        coEvery { api.login(any()) } returns Response.error(401, errorBody)

        val result = repository.login("test@gmail.com", "malapass")

        assertTrue(result.isFailure)
        assertEquals("Credenciales Incorrectas", result.exceptionOrNull()?.message)
    }

    @Test
    fun `login retorna Failure cuando hay excepcion de red`() = runBlocking {

        coEvery { api.login(any()) } throws java.io.IOException("No internet")

        val result = repository.login("a", "b")

        assertTrue(result.isFailure)
        assertEquals("No hay conexión a internet", result.exceptionOrNull()?.message)
    }
}