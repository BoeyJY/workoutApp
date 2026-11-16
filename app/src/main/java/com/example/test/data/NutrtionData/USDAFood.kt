package com.example.test.data.NutrtionData

data class UsdaFood(
    val fdcId: Long,
    val description: String,
    val dataType: String,
    val brandOwner: String? = null,
    val brandName: String? = null,
    val score: Float? = null,
    val servingSize: Float? = null,
    val servingSizeUnit: String? = null,
    val foodNutrients: List<FoodNutrient> = emptyList()
)

data class FoodNutrient(
    val nutrientId: Int? = null,
    val nutrientName: String? = null,
    val value: Float? = null,
    val unitName: String? = null
)


data class UsdaAbridgedFood(
    val fdcId: Long,
    val description: String,
    val dataType: String,
    val foodNutrients: List<abridgedFoodNutrient> = emptyList()
)

data class abridgedFoodNutrient(
    val number: Float,
    val name: String? = null,
    val amount: Float? = null,
    val unitName: String? = null
)

data class FoodSearchResponse(
    val foods: List<UsdaFood>
//    = emptyList()
)