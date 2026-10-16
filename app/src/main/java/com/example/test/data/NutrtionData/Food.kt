package com.example.test.data.NutrtionData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class Food (
    @PrimaryKey val id: Long,
    val name: String,
    val name_folded: String,
    val lang: String? = null,
    val brand: String? = null,
    val barcode: String?= null,
    val kcal_100g: Float,
    val protein_100g: Float,
    val fat_100g: Float,
    val carbs_100g: Float,
    val carbs_convention: String,
    val fiber_100g: Float? = null,
    val sugar_100g: Float? = null,
    val sat_fat_100g: Float? = null,
    val sodium_100g: Float? = null,
    val density_g_per_ml: Float? = null,
    val source: String,
    val quality_score: Int,
    @ColumnInfo(defaultValue = "1")
    val basic_rank: Int,
    @ColumnInfo(defaultValue = "1")
    val source_rank: Int,
    val alias_names: String? = null,
    val search_extra: String? = null,
    val country: String? = null,
    @ColumnInfo(defaultValue = "g")
    val nutrition_basis: String,
    val nutrition_metadata: String? = null
)