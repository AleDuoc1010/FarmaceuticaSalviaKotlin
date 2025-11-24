package com.example.farmaceuticasalvia.data.remote.api

import com.example.farmaceuticasalvia.data.remote.dto.LoginRequest
import com.example.farmaceuticasalvia.data.remote.dto.LoginResponse
import com.example.farmaceuticasalvia.data.remote.dto.PageResponse
import com.example.farmaceuticasalvia.data.remote.dto.RegisterRequest
import com.example.farmaceuticasalvia.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuariosApiService {

    @POST("usuarios/register")
    suspend fun register(@Body request: RegisterRequest): Response<UsuarioDto>

    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("usuarios")
    suspend fun getAllUsers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50 // Traemos bastantes
    ): Response<PageResponse<UsuarioDto>>

    @DELETE("usuarios/{uuid}")
    suspend fun deleteUser(@Path("uuid") uuid: String): Response<Void>
}