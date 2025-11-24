package com.example.farmaceuticasalvia.data.repository

import com.example.farmaceuticasalvia.data.remote.api.ExternalApiService
import com.example.farmaceuticasalvia.data.remote.dto.MindicadorResponse

class ExternalRepository(private val api: ExternalApiService) {

    suspend fun getIndicadores(): Result<MindicadorResponse> {
        return try {
            val response = api.getIndicadores()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error API Externa"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}