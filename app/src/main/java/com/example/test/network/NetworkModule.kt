package com.example.test.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

object NetworkModule {
    private const val BASE_URL = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/dist/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api: ExerciseApi = retrofit.create(ExerciseApi::class.java)
}

object NutritionNetworkModule {

    // for logging the http response to debugger, only for testing
//    val logging = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }
//    val client = OkHttpClient.Builder()
//        .addInterceptor(logging)   // addInterceptor, not addNetworkInterceptor
//        .build()
    private const val BASE_URL = "https://api.nal.usda.gov/fdc/v1/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
//        .client(client)
        // for logging the http response to debugger, only for testing
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: UsdaApiService = retrofit.create(UsdaApiService::class.java)
}

