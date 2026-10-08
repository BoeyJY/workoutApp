package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workoutSessions")
data class workoutSessions(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String // Store as milliseconds (e.g., System.currentTimeMillis() at midnight)
)