package com.example.farmaceuticasalvia.data.repository

import coil.network.HttpException
import com.example.farmaceuticasalvia.data.local.storage.UserPreferences
import com.example.farmaceuticasalvia.data.remote.api.UsuariosApiService
import com.example.farmaceuticasalvia.data.remote.dto.LoginRequest
import com.example.farmaceuticasalvia.data.remote.dto.RegisterRequest
import com.example.farmaceuticasalvia.data.remote.dto.UsuarioDto
import okio.IOException

class UserRepository (
    private val api: UsuariosApiService,
    private val preferences: UserPreferences
){

    suspend fun login(email: String, password: String): Result<UsuarioDto>{
        return try {
            val response = api.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!

                preferences.saveUserSession(
                    token = loginResponse.token,
                    uuid = loginResponse.usuario.uuid,
                    name = loginResponse.usuario.nombre,
                    email = loginResponse.usuario.email,
                    role = loginResponse.usuario.rol
                )

                Result.success(loginResponse.usuario)
            } else {
                val errorMsg = if (response.code() == 401) "Credenciales Incorrectas" else "Error en el servidor"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException){
            Result.failure(Exception("No hay conexión a internet"))
        } catch (e: HttpException){
            Result.failure(Exception("Error de red: ${e.message}"))
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Result<UsuarioDto> {
        return try {
            val request = RegisterRequest(
                nombre = name,
                email = email,
                phone = phone,
                password = password
            )
            val response = api.register(request)

            if (response.isSuccessful && response.body() != null){
                Result.success(response.body()!!)
            } else {
                val errorMsg = if (response.code() == 409) "El correo ya está registrado" else "Error al registrar usuario"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException){
            Result.failure(Exception("No hay conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error desconocido: ${e.localizedMessage}"))
        }
    }

    suspend fun logout(){
        preferences.clearSession()
    }

    suspend fun getAllUsers(): Result<List<UsuarioDto>> {
        return try {
            val response = api.getAllUsers()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.content)
            } else {
                Result.failure(Exception("Error al cargar usuarios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(uuid: String): Result<Unit> {
        return try {
            val response = api.deleteUser(uuid)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}