package com.example.farmaceuticasalvia.data.local.history

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.farmaceuticasalvia.data.local.products.ProductEntity

@Entity(
    tableName = "purchase_history",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["productId"])]
)

data class HistoryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val productId: Long,
    val quantity: Int,
    val phone: String,
    val timeStamp: Long
)