package com.example.test.data.NutrtionData

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.test.data.Exercise
import com.example.test.data.ExerciseDB
import com.example.test.network.NetworkModule
import com.example.test.network.NutritionApi
import com.example.test.network.NutritionNetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
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


    private val dao = FoodDB.getInstance(context).NutritionDAO()
//
//    fun searchFoodByName(input: String): Flow<List<Food>>{
//        return dao.searchFoodByName(input)
//    }
//
//    suspend fun getFoodServingSize(foodId: Long): Float{
//        return dao.getFoodServingSize(foodId)
//    }
//
//    suspend fun getFoodById(foodId: Long): Food{
//        return dao.getFoodById(foodId)
//    }

    suspend fun insertLoggedFood(
        foodId: Long,
        name: String,
        Date: String,
        category: String,
        weight: Float,
        servingSize: String,
        kcal: Float,
        carbs: Float,
        protein: Float,
        fat: Float
    ){
        val loggedFoodsEntity = logged_foods(
            foodId = foodId,
            name = name,
            Date = Date,
            category = category,
            weight = weight,
            servingSize = servingSize,
            kcal = kcal,
            carbs = carbs,
            protein = protein,
            fat = fat
        )
        dao.insertLoggedFood(loggedFoodsEntity)
    }


    suspend fun getLoggedFoodsByDate(date: String): List<logged_foods>{
        return dao.getLoggedFoodsByDate(date)
    }

    fun getDailyMacrosFlow(date: String): Flow<DailyMacros> = dao.getDailyMacrosFlow(date)

//    suspend fun getDailyMacros(date: String): DailyMacros{
//        return dao.getDailyMacros(date)
//    }

    suspend fun getLoggedFoodsById(id: Long): logged_foods{
        return dao.getLoggedFoodsById(id)
    }

    suspend fun updateLoggedFood(
        weight: Float,
        category: String,
        ss: String,
        kcal: Float,
        id: Long,
        carbs: Float,
        protein: Float,
        fat: Float){
        dao.updateLoggedFood(weight, category, ss, kcal, id, carbs, protein, fat)
    }

    suspend fun deleteLoggedFood(id: Long){
        dao.deleteLoggedFood(id)
    }

//
//    suspend fun searchUSDAFoods(query: String): FoodSearchResponse {
//        return NutritionNetworkModule.api
//            .searchFoods(
//                query = query,
//                apiKey = apiKey
//            )
////            .foods
//    }

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

    fun getCachedPaginated(
        query: String
        ): Flow<PagingData<UsdaFood>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { dao.getCachedPaginated(query) }
        ).flow.map{ data ->
            data.map {cachedFoods -> cachedFoods.toUsdaFood()}
        }
    }

    fun getUserCreatedPaginated(
        query: String
    ): Flow<PagingData<UsdaFood>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { dao.getUserCreatedPaginated(query) }
        ).flow.map{ data ->
            data.map {UserCreatedFoods -> UserCreatedFoods.toUsdaFood()}
        }
    }

    suspend fun getCachedFoodById(id: Long): UsdaAbridgedFood?{
        return dao.getCachedFoodById(id)?.toUsdaAbridgedFood()
    }

    suspend fun getUserCreatedById(id: Long): UsdaAbridgedFood?{
        return dao.getUserCreatedById(id)?.toUsdaAbridgedFood()
    }

    suspend fun deleteFromCache(id: Long){
        dao.deleteFromCache(id)
    }

    suspend fun deleteUserCreated(id: Long){
        dao.deleteUserCreated(id)
    }

    fun CachedFoods.toUsdaAbridgedFood(): UsdaAbridgedFood{
        return UsdaAbridgedFood(
            fdcId = id,
            description = description,
            dataType = dataType,
            // OK SO
            // I am mapping all of it back to the USDA abridged Food list structure
            // At first i thought I'd only need the kcal so I just had the first FoodNutrient
            // Then I realised that FoodAdd needs the same structure to not have to do a bunch of if statements
            // I then realised there must be a way to just store the USDA food instead of creating a new object CachedFoods
            // However, the food details from USDA i use to insert into the table, don't have brand name and serving size
            // Also, I'd have to map the JSON list of FoodNutrients anyways
            // I made the abridgedFoodNutrient data class have nullable names and unitName
            // This is just for my current convenience lol since they arent needed now
            // Otherwise they need to be added to cached foods as well
            // BUT there's probably a much better way to do this
            // Future me if you have to refactor this, I'm sorry LOL, rn I CBA
            foodNutrients = listOf(
                abridgedFoodNutrient(
                    number = 1008F,
                    amount = kcal,
                    unitName = "kcal"
                ),
                abridgedFoodNutrient(
                    number = 1003F,
                    amount = protein
                ),
                abridgedFoodNutrient(
                    number = 1004F,
                    amount = totalLipid
                ),
                abridgedFoodNutrient(
                    number = 1005F,
                    amount = carbs
                ),
                abridgedFoodNutrient(
                    number = 1063F,
                    amount = sugars
                ),
                abridgedFoodNutrient(
                    number = 1079F,
                    amount = fiber
                ),
                abridgedFoodNutrient(
                    number = 1087F,
                    amount = calcium
                ),
                abridgedFoodNutrient(
                    number = 1089F,
                    amount = iron
                ),
                abridgedFoodNutrient(
                    number = 1092F,
                    amount = potassium
                ),
                abridgedFoodNutrient(
                    number = 1093F,
                    amount = sodium
                ),
                abridgedFoodNutrient(
                    number = 1106F,
                    amount = vitaminA
                ),
                abridgedFoodNutrient(
                    number = 1162F,
                    amount = vitaminC
                ),
                abridgedFoodNutrient(
                    number = 1165F,
                    amount = thiamin
                ),
                abridgedFoodNutrient(
                    number = 1167F,
                    amount = niacin
                ),
                abridgedFoodNutrient(
                    number = 1177F,
                    amount = folate
                ),
                abridgedFoodNutrient(
                    number = 1253F,
                    amount = cholesterol
                ),
                abridgedFoodNutrient(
                    number = 1257F,
                    amount = transFat
                ),
                abridgedFoodNutrient(
                    number = 1258F,
                    amount = saturatedFat
                )
            )
            )
    }
    fun CachedFoods.toUsdaFood(): UsdaFood {
        return UsdaFood(
            fdcId = id,
            description = description,
            dataType = dataType,
            brandName = brandName,
            servingSize = servingSize,
            // OK SO
            // I am mapping all of it back to the USDA Food list structure
            // At first i thought I'd only need the kcal so I just had the first FoodNutrient
            // Then I realised that FoodAdd needs the same structure to not have to do a bunch of if statements
            // I then realised there must be a way to just store the USDA food instead of creating a new object CachedFoods
            // However, the food details from USDA i use to insert into the table, don't have brand name and serving size
            // Also, I'd have to map the JSON list of FoodNutrients anyways
            // BUT there's probably a much better way to do this
            // Future me if you have to refactor this, I'm sorry LOL, rn I CBA
            // I only use KCAL from foodNutrients, all the rest is not needed as of writing, uncomment if needed.
            foodNutrients = listOf(
                FoodNutrient(
                    nutrientId = 1008,
                    nutrientName = "Energy",
                    value = kcal,
                    unitName = "kcal"
                )
//                ,
//                FoodNutrient(
//                    nutrientId = 1003,
//                    value = protein
//                ),
//                FoodNutrient(
//                    nutrientId = 1004,
//                    value = totalLipid
//                ),
//                FoodNutrient(
//                    nutrientId = 1005,
//                    value = carbs
//                ),
//                FoodNutrient(
//                    nutrientId = 1063,
//                    value = sugars
//                ),
//                FoodNutrient(
//                    nutrientId = 1079,
//                    value = fiber
//                ),
//                FoodNutrient(
//                    nutrientId = 1087,
//                    value = calcium
//                ),
//                FoodNutrient(
//                    nutrientId = 1089,
//                    value = iron
//                ),
//                FoodNutrient(
//                    nutrientId = 1092,
//                    value = potassium
//                ),
//                FoodNutrient(
//                    nutrientId = 1093,
//                    value = sodium
//                ),
//                FoodNutrient(
//                    nutrientId = 1106,
//                    value = vitaminA
//                ),
//                FoodNutrient(
//                    nutrientId = 1162,
//                    value = vitaminC
//                ),
//                FoodNutrient(
//                    nutrientId = 1165,
//                    value = thiamin
//                ),
//                FoodNutrient(
//                    nutrientId = 1167,
//                    value = niacin
//                ),
//                FoodNutrient(
//                    nutrientId = 1177,
//                    value = folate
//                ),
//                FoodNutrient(
//                    nutrientId = 1253,
//                    value = cholesterol
//                ),
//                FoodNutrient(
//                    nutrientId = 1257,
//                    value = transFat
//                ),
//                FoodNutrient(
//                    nutrientId = 1258,
//                    value = saturatedFat
//                )
            )
        )
    }

    fun UserCreatedFoods.toUsdaAbridgedFood(): UsdaAbridgedFood{
        return UsdaAbridgedFood(
            fdcId = id,
            description = description,
            dataType = "Created",
            // Same reason as above
            // Genuinely there's probably a much much cleaner way to do this
            // but rn I cba
            foodNutrients = listOf(
                abridgedFoodNutrient(
                    number = 1008F,
                    amount = kcal,
                    unitName = "kcal"
                ),
                abridgedFoodNutrient(
                    number = 1003F,
                    amount = protein
                ),
                abridgedFoodNutrient(
                    number = 1004F,
                    amount = totalLipid
                ),
                abridgedFoodNutrient(
                    number = 1005F,
                    amount = carbs
                ),
                abridgedFoodNutrient(
                    number = 1063F,
                    amount = sugars
                ),
                abridgedFoodNutrient(
                    number = 1079F,
                    amount = fiber
                ),
                abridgedFoodNutrient(
                    number = 1087F,
                    amount = calcium
                ),
                abridgedFoodNutrient(
                    number = 1089F,
                    amount = iron
                ),
                abridgedFoodNutrient(
                    number = 1092F,
                    amount = potassium
                ),
                abridgedFoodNutrient(
                    number = 1093F,
                    amount = sodium
                ),
                abridgedFoodNutrient(
                    number = 1106F,
                    amount = vitaminA
                ),
                abridgedFoodNutrient(
                    number = 1162F,
                    amount = vitaminC
                ),
                abridgedFoodNutrient(
                    number = 1165F,
                    amount = thiamin
                ),
                abridgedFoodNutrient(
                    number = 1167F,
                    amount = niacin
                ),
                abridgedFoodNutrient(
                    number = 1177F,
                    amount = folate
                ),
                abridgedFoodNutrient(
                    number = 1253F,
                    amount = cholesterol
                ),
                abridgedFoodNutrient(
                    number = 1257F,
                    amount = transFat
                ),
                abridgedFoodNutrient(
                    number = 1258F,
                    amount = saturatedFat
                )
            )
        )
    }
    fun UserCreatedFoods.toUsdaFood(): UsdaFood {
        return UsdaFood(
            fdcId = id,
            description = description,
            dataType = "Created",
            brandName = brandName,
            servingSize = servingSize,
            // Same reason as above
            foodNutrients = listOf(
                FoodNutrient(
                    nutrientId = 1008,
                    nutrientName = "Energy",
                    value = kcal,
                    unitName = "kcal"
                )
            )
        )
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


    suspend fun insertUserCreatedFood(USF: UserCreatedFoods){
        dao.insertUserCreatedFood(USF)
    }

    }