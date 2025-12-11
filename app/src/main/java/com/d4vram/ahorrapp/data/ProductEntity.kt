package com.d4vram.ahorrapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val barcode: String,
    val name: String,
    val brand: String? = null,
    val moreInfo: String? = null,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)