package com.example.farmaceuticasalvia.data.local.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert
    suspend fun insert(item: HistoryItemEntity)

    @Query("SELECT P.*, H.quantity, H.phone, H.timestamp, H.id AS historyId FROM purchase_history H INNER JOIN products P ON H.productId = P.id ORDER BY H.timeStamp DESC")
    fun getHistory(): Flow<List<PurchaseRecord>>

    @Query("DELETE FROM purchase_history WHERE id = :historyItemId")
    suspend fun delete(historyItemId: Long)

    @Query("DELETE FROM purchase_history")
    suspend fun clearHistory()
}