package com.example.farmaceuticasalvia.data.remote.api

import com.example.farmaceuticasalvia.data.remote.dto.MindicadorResponse
import retrofit2.Response
import retrofit2.http.GET

interface ExternalApiService {
    @GET("api")
    suspend fun getIndicadores(): Response<MindicadorResponse>
}