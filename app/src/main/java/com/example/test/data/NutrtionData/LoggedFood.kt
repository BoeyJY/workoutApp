package com.example.test.data.NutrtionData


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logged_foods")
data class logged_foods (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodId: Long,
    val name: String,
    val Date: String,
    val category: String,
    val weight: Float,
    val servingSize: String,
    val kcal: Float
)