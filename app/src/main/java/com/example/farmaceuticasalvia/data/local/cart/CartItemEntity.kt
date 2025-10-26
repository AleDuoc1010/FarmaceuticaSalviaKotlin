package com.example.farmaceuticasalvia.data.local.cart

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.farmaceuticasalvia.data.local.products.ProductEntity

@Entity(
    tableName = "cart_items",
    primaryKeys = ["productId"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)


data class CartItemEntity (
    val productId: Long,
    val quantity: Int
)