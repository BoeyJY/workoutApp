package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessionExercises")
data class sessionExercises(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long, // Foreign key to WorkoutSession
    val exerciseId: String, // Foreign key to Exercise
    val orderIndex: Int = 0 // So you can drag/reorder cards later
)