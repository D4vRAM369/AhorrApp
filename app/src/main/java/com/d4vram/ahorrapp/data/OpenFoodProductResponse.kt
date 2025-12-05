package com.d4vram.ahorrapp.data

import com.google.gson.annotations.SerializedName

data class OpenFoodProductResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("status_verbose") val statusVerbose: String?,
    @SerializedName("product") val product: OpenFoodProduct?
)

data class OpenFoodProduct(
    @SerializedName("product_name") val productName: String?,
    @SerializedName("brands") val brands: String?
)
