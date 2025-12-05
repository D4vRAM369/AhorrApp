package com.d4vram.ahorrapp.data

import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodApiService {

    // Ejemplo: https://world.openfoodfacts.org/api/v2/product/8410076474438.json
    @GET("api/v2/product/{barcode}.json")
    suspend fun fetchProduct(
        @Path("barcode") barcode: String
    ): OpenFoodProductResponse
}
