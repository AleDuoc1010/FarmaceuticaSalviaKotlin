package com.example.farmaceuticasalvia.data.local.history

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.farmaceuticasalvia.data.local.products.ProductEntity


data class PurchaseRecord(
    @ColumnInfo("historyId")
    val id: Long,
    @Embedded
    val product: ProductEntity,
    val quantity: Int,
    val phone: String,
    val timeStamp: Long
)