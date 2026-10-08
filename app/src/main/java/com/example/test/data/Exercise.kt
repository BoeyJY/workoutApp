package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// This class represents both GitHub exercises and user‑created ones.
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String,           // GitHub uses "id" like "0001"
    val name: String,
    val force: String? = null,
    val level: String? = null,
    val mechanic: String? = null,
    val equipment: String? = null,
    val instructions: String? = null,
    val category: String? = null,
    val primaryMuscles: String? = null,          // you can store as JSON string or a separate table
    val secondaryMuscles: String? = null,
    val imagePath: String? = null,        // relative path from GitHub
    val isUserCreated: Boolean = false,   // true if added by the user
    val createdAt: Long = System.currentTimeMillis()
)