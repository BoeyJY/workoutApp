package com.example.test.data.NutrtionData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_servings")
data class food_servings (
    @PrimaryKey val id: Int,
    val food_id: Int,
    val label: String,
    val grams: Float,
    val is_default: Int,
    val sort_order: Int
)


