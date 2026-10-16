package com.example.test.data.NutrtionData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_aliases")
data class food_aliases (
    @PrimaryKey val id: Int,
    val food_id: Int,
    val lang: String,
    val name: String
)