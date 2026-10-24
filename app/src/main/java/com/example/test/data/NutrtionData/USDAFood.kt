package com.example.test.data.NutrtionData

data class UsdaFood(
    val fdcId: Long,
    val description: String
)

data class FoodSearchResponse(
    val foods: List<UsdaFood> = emptyList()
)