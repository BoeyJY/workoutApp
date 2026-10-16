package com.example.test.network


import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

interface NutritionApi {
    @Streaming
    @GET("lethio-off-derived-build19.sqlite")
    suspend fun fetchFood(): ResponseBody
}