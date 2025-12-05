package com.d4vram.ahorrapp.data

import com.google.gson.annotations.SerializedName

data class OpenFoodProductResponse(
    val status: Int,
    @SerializedName("status_verbose") val statusVerbose: String?,
    val product: OpenFoodProduct?
)

data class OpenFoodProduct(
    @SerializedName("product_name") val productName: String?,
    val brands: String?
)
