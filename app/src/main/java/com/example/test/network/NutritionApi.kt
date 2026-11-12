package com.example.test.network


import com.example.test.data.NutrtionData.FoodSearchResponse
import com.example.test.data.NutrtionData.UsdaAbridgedFood
import com.example.test.data.NutrtionData.UsdaFood
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

//    Contains information from Open Food Facts (https://world.openfoodfacts.org),
//    which is made available under the Open Database License (ODbL) 1.0.
//    (c) Open Food Facts contributors.
interface NutritionApi {
    @Streaming
    @GET("lethio-off-derived-build19.sqlite")
    suspend fun fetchFood(): ResponseBody
}

interface UsdaApiService {

    @GET("foods/search")
    suspend fun searchFoods(
        @Query("query") query: String,
        @Query("dataType") dataType: List<String> = listOf("Branded", "Foundation"),
        @Query("api_key") apiKey: String,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): FoodSearchResponse

    @GET("food/{fdcId}")
    suspend fun getFoodById(
        @Path("fdcId") fdcId: Long,
        @Query("format") format: String = "abridged", // "full" or "abridged"
        @Query("api_key") apiKey: String
    ): UsdaAbridgedFood
}

