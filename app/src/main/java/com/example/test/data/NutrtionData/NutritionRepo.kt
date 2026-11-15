package com.example.test.data.NutrtionData

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.test.data.Exercise
import com.example.test.data.ExerciseDB
import com.example.test.network.NetworkModule
import com.example.test.network.NutritionApi
import com.example.test.network.NutritionNetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NutritionRepo(private val context: Context,
                    private val apiKey: String
) {

    private val dbName = "lethio-food-db.sqlite"
    private val dbFile: File get() = context.getDatabasePath(dbName)
    private val mutex = Mutex()

    private val dao = FoodDB.getInstance(context).NutritionDAO()

    fun searchFoodByName(input: String): Flow<List<Food>>{
        return dao.searchFoodByName(input)
    }

    suspend fun getFoodServingSize(foodId: Long): Float{
        return dao.getFoodServingSize(foodId)
    }

    suspend fun getFoodById(foodId: Long): Food{
        return dao.getFoodById(foodId)
    }

    suspend fun insertLoggedFood(
        foodId: Long,
        name: String,
        Date: String,
        category: String,
        weight: Float,
        servingSize: String,
        kcal: Float
    ){
        val loggedFoodsEntity = logged_foods(
            foodId = foodId,
            name = name,
            Date = Date,
            category = category,
            weight = weight,
            servingSize = servingSize,
            kcal = kcal
        )
        dao.insertLoggedFood(loggedFoodsEntity)
    }


    suspend fun getLoggedFoodsByDate(date: String): List<logged_foods>{
        return dao.getLoggedFoodsByDate(date)
    }

    suspend fun getDailyMacros(date: String): DailyMacros{
        return dao.getDailyMacros(date)
    }

    suspend fun getLoggedFoodsById(id: Long): logged_foods{
        return dao.getLoggedFoodsById(id)
    }

    suspend fun updateLoggedFood(weight: Float, category: String, ss: String, kcal: Float, id: Long){
        dao.updateLoggedFood(weight, category, ss, kcal, id)
    }

    suspend fun deleteLoggedFood(id: Long){
        dao.deleteLoggedFood(id)
    }


    suspend fun searchUSDAFoods(query: String): FoodSearchResponse {
        return NutritionNetworkModule.api
            .searchFoods(
                query = query,
                apiKey = apiKey
            )
//            .foods
    }

    fun searchPaginated(
        query: String
    ): Flow<PagingData<UsdaFood>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                FoodPagingSource(
                    NutritionNetworkModule.api,
                    query = query,
                    apiKey = apiKey
                )
            }
        ).flow
}

    suspend fun getUsdaFoodById(id: Long): UsdaAbridgedFood {
        return NutritionNetworkModule.api.getFoodById(id, apiKey = apiKey)
    }


    suspend fun insertCacheFood(cachedFood: CachedFoods){
        dao.insert(cachedFood)
    }

    suspend fun updateCacheTouch(cacheId: Long){
        dao.updateCacheTouch(cacheId)
    }

    suspend fun insertWithEviction(cachedFood: CachedFoods){
        dao.insertWithEviction(cachedFood)
    }

    }