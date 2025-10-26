package com.example.farmaceuticasalvia.data.local.cart

import androidx.room.Embedded
import com.example.farmaceuticasalvia.data.local.products.ProductEntity

data class CartItem(
    @Embedded
    val product: ProductEntity,
    val quantity: Int
)