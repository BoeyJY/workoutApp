package com.example.test.data

import androidx.room.Embedded
import androidx.room.Relation

// This is NOT an Entity. It's a data class used to hold the joined query result.
data class ExerciseCardData(
    @Embedded val sessionExercises: sessionExercises,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionExerciseId"
    )
    val sets: List<sessionSets>,
    val exerciseName: String // We'll get this from the JOIN
)