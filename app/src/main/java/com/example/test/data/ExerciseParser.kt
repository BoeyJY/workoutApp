package com.example.test.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ExerciseParser {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter = moshi.adapter<List<GitHubExercise>>(Types.newParameterizedType(List::class.java, GitHubExercise::class.java))

    fun parseExerciseJson(json: String): List<Exercise> {
        val gitHubExercises = jsonAdapter.fromJson(json) ?: emptyList()
        return gitHubExercises.map { gitHubEx ->
            Exercise(
                id = gitHubEx.id,
                name = gitHubEx.name,
                force = gitHubEx.force,
                level = gitHubEx.level,
                mechanic = gitHubEx.mechanic,
                equipment = gitHubEx.equipment,
                instructions = gitHubEx.instructions?.joinToString(separator = ","),
                category = gitHubEx.category,
                // Convert muscles list to a comma-separated string for simplicity
                primaryMuscles = gitHubEx.primaryMuscles?.joinToString(separator = ","),
                secondaryMuscles = gitHubEx.secondaryMuscles?.joinToString(separator = ","),
                // Take the first image if available, or null
                imagePath = gitHubEx.images?.firstOrNull(),
                isUserCreated = false
            )
        }
    }
}