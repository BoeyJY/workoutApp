package com.example.test.data.NutrtionData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.test.data.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDAO{

    @Insert
    suspend fun insertLoggedFood(loggedFoods: logged_foods)

    @Query("SELECT * FROM foods ORDER BY name ASC")
    suspend fun getAllFood(): List<Food>

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :input || '%' ORDER BY name LIMIT :limit")
    fun searchFoodByName(input: String, limit: Int = 100): Flow<List<Food>>

    @Query("SELECT * FROM foods WHERE id = :foodId")
    suspend fun getFoodById(foodId: Long): Food

    @Query("SELECT grams FROM food_servings WHERE food_id = :foodId")
    suspend fun getFoodServingSize(foodId: Long): Float

    @Query("SELECT * FROM logged_foods WHERE date = :date")
    suspend fun getLoggedFoodsByDate(date: String): List<logged_foods>
}