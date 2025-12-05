package com.d4vram.ahorrapp.data

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/prices/add")
    suspend fun uploadPrice(@Body payload: PricePayload)
}
