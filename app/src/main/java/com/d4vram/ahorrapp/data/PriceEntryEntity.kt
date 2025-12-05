package com.d4vram.ahorrapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_entries")
data class PriceEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val barcode: String,
    val productName: String?,
    val supermarket: String,
    val price: Double,
    val timestamp: Long
)
