package com.example.test.data.NutrtionData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_barcodes")
data class food_barcodes (
    @PrimaryKey val barcode: String,
    val food_id: Int
)