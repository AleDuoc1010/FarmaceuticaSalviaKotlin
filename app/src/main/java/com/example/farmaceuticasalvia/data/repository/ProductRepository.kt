package com.example.farmaceuticasalvia.data.repository

import com.example.farmaceuticasalvia.data.local.products.ProductDao
import com.example.farmaceuticasalvia.data.local.products.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val dao: ProductDao
) {

    fun getAllProducts(): Flow<List<ProductEntity>>{
        return dao.getAll()
    }

    fun GetFeaturedProducts(): Flow<List<ProductEntity>>{
        return dao.getFeatured()
    }

    suspend fun getProductById(id: Long): ProductEntity?{
        return dao.getById(id)
    }

}