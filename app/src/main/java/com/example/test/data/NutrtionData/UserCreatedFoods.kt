package com.example.test.data.NutrtionData

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "UserCreatedFoods",
    indices = [
        Index(value = ["description"], unique = true)
    ])
data class UserCreatedFoods(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val servingSize: Float = 100F,
    val protein: Float = 0F,
    val totalLipid: Float = 0F,
    val carbs: Float = 0F,
    val kcal: Float = 0F,
    val sugars: Float = 0F,
    val fiber: Float = 0F,
    val calcium: Float = 0F,
    val iron: Float = 0F,
    val potassium: Float = 0F,
    val sodium: Float = 0F,
    val vitaminA: Float = 0F,
    val vitaminC: Float = 0F,
    val thiamin: Float = 0F,
    val niacin: Float = 0F,
    val folate: Float = 0F,
    val cholesterol: Float = 0F,
    val transFat: Float = 0F,
    val saturatedFat: Float = 0F,
    val lastUsed: Long = System.currentTimeMillis()
)