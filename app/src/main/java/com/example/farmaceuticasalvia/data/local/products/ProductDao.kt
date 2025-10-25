package com.example.farmaceuticasalvia.data.local.products

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(product: ProductEntity): Long

    @Query("SELECT * FROM products ORDER BY id ASC")
    suspend fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE featured = 1")
    suspend fun getFeatured(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): ProductEntity?

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}