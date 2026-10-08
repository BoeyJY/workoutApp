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