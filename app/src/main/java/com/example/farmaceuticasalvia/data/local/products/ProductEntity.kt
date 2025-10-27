package com.example.farmaceuticasalvia.data.local.products

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val descr: String,
    val price: Int,
    val imageRes: Int,
    val featured: Boolean,
    val requireRecipe: Boolean = false
){
}