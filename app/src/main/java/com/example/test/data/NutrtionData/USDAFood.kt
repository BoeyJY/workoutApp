package com.example.test.data.NutrtionData

data class UsdaFood(
    val fdcId: Long,
    val description: String,
    val dataType: String,
    val brandOwner: String? = null,
    val score: Float
)

data class FoodSearchResponse(
    val foods: List<UsdaFood>
//    = emptyList()
)