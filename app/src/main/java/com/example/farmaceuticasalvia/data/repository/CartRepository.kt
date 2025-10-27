package com.example.farmaceuticasalvia.data.repository

import com.example.farmaceuticasalvia.data.local.cart.CartDao
import com.example.farmaceuticasalvia.data.local.cart.CartItemEntity
import com.example.farmaceuticasalvia.data.local.products.ProductEntity

class CartRepository(
    private val cartDao: CartDao
){

    val cartItems = cartDao.getCartContents()


    suspend fun addToCart(product: ProductEntity, quantity: Int){

        val existingItem = cartDao.getEntityByProductId(product.id)

        if(existingItem != null) {
            val newQuantity = existingItem.quantity + quantity
            cartDao.upsert(CartItemEntity(product.id, newQuantity))
        } else{
            cartDao.upsert(CartItemEntity(product.id, quantity))
        }
    }

    suspend fun removeFromCart(productId: Long){
        cartDao.delete(productId)
    }

    suspend fun clearCart(){
        cartDao.clearCart()
    }
}