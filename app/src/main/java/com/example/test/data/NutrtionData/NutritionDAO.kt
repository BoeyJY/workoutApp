package com.example.test.data.NutrtionData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import androidx.room3.Transaction
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

    @Query("SELECT * FROM logged_foods WHERE id = :id")
    suspend fun getLoggedFoodsById(id: Long): logged_foods

    @Query("UPDATE logged_foods SET weight = :weight, category = :category, " +
            "servingSize = :ss, kcal = :kcal WHERE id = :id")
    suspend fun updateLoggedFood(weight: Float, category: String, ss: String, kcal: Float, id: Long)

    @Query("DELETE FROM logged_foods WHERE id = :id")
    suspend fun deleteLoggedFood(id: Long)

    @Query("SELECT IFNULL(SUM(lf.kcal), 0) as cal, " +
            "IFNULL(SUM(f.protein_100g/100*lf.weight), 0)as protein, " +
            "IFNULL(SUM(f.carbs_100g/100*lf.weight), 0) as carb, " +
            "IFNULL(SUM(f.fat_100g/100*lf.weight), 0) as fat " +
            "FROM logged_foods lf INNER JOIN foods f " +
            "ON lf.foodId = f.id WHERE date = :date")
    suspend fun getDailyMacros(date: String): DailyMacros



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(food: CachedFoods)

    @Query("UPDATE CachedFoods SET lastUsed = :lastUsed WHERE id = :cacheFoodId")
    suspend fun updateCacheTouch(
        cacheFoodId: Long,
        lastUsed: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM CachedFoods")
    suspend fun count(): Int

    @Query("""
        DELETE FROM CachedFoods
        WHERE id IN (
            SELECT id FROM CachedFoods
            ORDER BY lastUsed ASC
            LIMIT :n
        )
    """)
    suspend fun deleteOldest(n: Int)

    @Transaction
    suspend fun insertWithEviction(food: CachedFoods, max: Int = 10) {
        insert(food)
        updateCacheTouch(food.id)

        val n = count()

        if (n > max) {
            deleteOldest(n - max)
        }
    }
}

data class DailyMacros(
    val cal: Float? = 0F,
    val protein: Float? = 0F,
    val carb: Float? = 0F,
    val fat: Float? = 0F
)