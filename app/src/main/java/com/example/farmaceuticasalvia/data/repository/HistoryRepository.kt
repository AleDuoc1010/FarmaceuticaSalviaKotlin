package com.example.farmaceuticasalvia.data.repository

import com.example.farmaceuticasalvia.data.remote.api.PedidosApiService
import com.example.farmaceuticasalvia.data.remote.dto.PedidoResponse
import okio.IOException

class HistoryRepository(
    private val api: PedidosApiService
) {

    suspend fun getHistory(): Result<List<PedidoResponse>> {
        return try {
            val response = api.getHistorial()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar historial: ${response.code()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("No hay conexión a internet"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromHistory(pedidoId: Long): Result<Unit> {
        return try {
            val response = api.deleteHistoryItem(pedidoId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar el registro"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearHistory(): Result<Unit> {
        return try {
            val response = api.clearHistory()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al vaciar el historial"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}