package com.example.test.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/dist/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api: ExerciseApi = retrofit.create(ExerciseApi::class.java)
}

object NutritionNetworkModule {
//    Contains information from Open Food Facts (https://world.openfoodfacts.org),
//    which is made available under the Open Database License (ODbL) 1.0.
//    (c) Open Food Facts contributors.
    private const val BASE_URL = "https://github.com/TerjeRu/lethio-food-db/releases/download/off-derived-build19/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
//        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api: NutritionApi = retrofit.create(NutritionApi::class.java)
}