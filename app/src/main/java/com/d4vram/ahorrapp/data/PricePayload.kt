package com.d4vram.ahorrapp.data

data class PricePayload(
    val barcode: String,
    val supermarket: String,
    val price: Double,
    val timestamp: Long,
    val productName: String? = null,
    val brand: String? = null,
    val moreInfo: String? = null
)
