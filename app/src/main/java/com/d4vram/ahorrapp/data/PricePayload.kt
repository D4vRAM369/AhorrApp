package com.d4vram.ahorrapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PricePayload(
    val barcode: String,
    val supermarket: String,
    val price: Double,
    val timestamp: Long, // Note: DB has created_at, but we handle logic. If we send this, we need to map it or ignore it if DB generates it. 
                         // For now let's keep it as timestamp in payload. Ideally we map it to no field if not used in DB insert, 
                         // or we map it to specific column if we want to override creation time.
                         // Let's assume we want to send it roughly as is or just for API compatibility.
                         // Actually, for Supabase insert, we should match the table columns.
    
    @SerialName("product_name")
    val productName: String? = null,
    
    val brand: String? = null,
    
    @SerialName("more_info")
    val moreInfo: String? = null
)
