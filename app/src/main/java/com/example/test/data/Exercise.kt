package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

// This class represents both GitHub exercises and user‑created ones.
@Entity(tableName = "exercises",
    indices = [
        Index(value = ["name"], unique = true)
    ])
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


data class MaxWeightPerDay(
    val date: String,      // "yyyy-MM-dd" straight from SQLite
    val weight: Float  // match your column type: Int/Float/Double
)