package com.example.test.data

import com.squareup.moshi.Json

data class GitHubExercise(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "force") val force: String? = null,
    @Json(name = "level") val level: String? = null,
    @Json(name = "mechanic") val mechanic: String? = null,
    @Json(name = "equipment") val equipment: String? = null,
    @Json(name = "instructions") val instructions: List<String>? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "primaryMuscles") val primaryMuscles: List<String>? = null,
    @Json(name = "secondaryMuscles") val secondaryMuscles: List<String>? = null,
    @Json(name = "images") val images: List<String>? = null
)