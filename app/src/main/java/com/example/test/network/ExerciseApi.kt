package com.example.test.network


import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface ExerciseApi {
    @GET("exercises.json")
    suspend fun fetchExercises(): ResponseBody   // we'll parse it as a plain JSON string
}