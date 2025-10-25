package com.example.farmaceuticasalvia.data.repository

import com.example.farmaceuticasalvia.data.local.products.ProductDao
import com.example.farmaceuticasalvia.data.local.products.ProductEntity

class ProductRepository(
    private val dao: ProductDao
) {

    suspend fun getAllProducts(): List<ProductEntity>{
        return dao.getAll()
    }

    suspend fun getProductById(id: Long): ProductEntity?{
        return dao.getById(id)
    }
}