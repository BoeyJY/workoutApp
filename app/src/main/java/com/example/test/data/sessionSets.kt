package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessionSets")
data class sessionSets(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionExerciseId: Long, // Foreign key to sessionExercise
    val setNumber: Int,
    val comment: String? = null,
    val weight: Float,
    val reps: Int
)