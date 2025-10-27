package com.example.farmaceuticasalvia.data.repository

import com.example.farmaceuticasalvia.data.local.history.HistoryDao
import com.example.farmaceuticasalvia.data.local.history.HistoryItemEntity
import com.example.farmaceuticasalvia.data.local.products.ProductEntity

class HistoryRepository(
    private val historyDao: HistoryDao
) {

    val purchaseHistory = historyDao.getHistory()

    suspend fun addToHistory(product: ProductEntity, quantity: Int, phone: String){
        val historyItem = HistoryItemEntity(
            productId = product.id,
            quantity = quantity,
            phone = phone,
            timeStamp = System.currentTimeMillis()
        )
        historyDao.insert(historyItem)
    }

    suspend fun removeFromHistory(historyItemId: Long) {
        historyDao.delete(historyItemId)
    }

    suspend fun clearHistory(){
        historyDao.clearHistory()
    }
}