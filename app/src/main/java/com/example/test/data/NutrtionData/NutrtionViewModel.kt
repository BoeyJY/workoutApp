package com.example.test.data.NutrtionData


import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.test.BuildConfig
import com.example.test.data.Exercise
import com.example.test.nutrition.newFoodData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.*


sealed interface FoodUiState {
    data object Idle : FoodUiState
    data object Loading : FoodUiState
    data class Success(val foods: StateFlow<List<UsdaFood>>) : FoodUiState
    data class Error(val message: String) : FoodUiState
}

enum class FoodSource { CACHE, API, USER_CREATED }
class NutritionViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = NutritionRepo(application,
        apiKey =
            BuildConfig.USDA_APIKEY
    )



//    fun searchUSDAFoods(query: String) {
////        if (query.isBlank()) return
//
//        viewModelScope.launch {
////            _uiState.value = FoodUiState.Loading
//
//            try {
//                val response = repo.searchUSDAFoods(query)
//                response.foods.forEach { food ->
//                    Log.d("USDA_API", "ID: ${food.fdcId} | ${food.description} | ${food.dataType} | " +
//                            "${food.brandOwner} | ${food.score}")
//                }
////                _uiState.value = FoodUiState.Success(foods)
//            } catch (e: Exception) {
////                _uiState.value = FoodUiState.Error(
//                    Log.e("USDA Error: ", e.toString())
//            }
//        }
//    }


//    val searchResults: Flow<PagingData<UsdaFood>> = repo
//        .searchPaginated()
//        .cachedIn(viewModelScope)

//    private val _dbState = MutableStateFlow<DbState>(DbState.Checking)
//    val dbState: StateFlow<DbState> = _dbState.asStateFlow()
//
//    init {
//        viewModelScope.launch {
////            repo.refreshFoodFromNetwork()
//            try{
//
//            }
//        }
//
//    }

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    private val query = MutableStateFlow("")
    val searchQuery: StateFlow<String> = query.asStateFlow()

    fun onSearchClick() {
        _isSearching.value = true
    }

    fun onSearchCancel() {
        query.value = ""
        _isSearching.value = false
    }

    fun onSearchTextChanged(text: String) {
        query.value = text
    }

    private val _foodSource = MutableStateFlow(FoodSource.CACHE)
    val foodSource: StateFlow<FoodSource> = _foodSource.asStateFlow()

    fun setFoodSource(source: FoodSource) {
        _foodSource.value = source
    }

//
//    private val _uiState =
//        MutableStateFlow<FoodUiState>(FoodUiState.Idle)
//
//    val uiState: StateFlow<FoodUiState> =
//        _uiState.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<UsdaFood>> = combine(
        query
        .debounce(500.milliseconds)
        .distinctUntilChanged(),
        _foodSource
    ){
        query, source ->
        query.trim() to source
    }
        .flatMapLatest { (query, source) ->
            when (source) {
                FoodSource.API -> {
                    if (query.isBlank()) {
                        flowOf(PagingData.empty())
                    } else {
                        repo.searchPaginated(query)
                    }
                }
                FoodSource.CACHE -> {
                    repo.getCachedPaginated(query)
                }

                FoodSource.USER_CREATED -> {
                    repo.getUserCreatedPaginated(query)
                }
            }.cachedIn(viewModelScope)

        }
//        .stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(5000),
//        emptyList()
//        )


    suspend fun getUsdaFoodById(id: Long): UsdaAbridgedFood{
        return repo.getUsdaFoodById(id)
    }

    suspend fun getCachedFoodById(id: Long): UsdaAbridgedFood?{
        return repo.getCachedFoodById(id)
    }

    suspend fun getUserCreatedById(id: Long): UsdaAbridgedFood?{
        return repo.getUserCreatedById(id)
    }

    suspend fun searchCacheThenApi(id: Long): UsdaAbridgedFood{
        val food = getCachedFoodById(id)

        if(food == null){
            return getUsdaFoodById(id)
        }
        else{
            return food
        }
    }

    private val _foodLoadState = MutableStateFlow<FoodLoadState>(FoodLoadState.Loading)
    val foodLoadState: StateFlow<FoodLoadState> = _foodLoadState.asStateFlow()


    fun loadFood(id: Long){
        viewModelScope.launch {
            _foodLoadState.value = FoodLoadState.Loading
            val cachedFood = getCachedFoodById(id)
            if (cachedFood != null) {
                _foodLoadState.value = FoodLoadState.Success(cachedFood)
                return@launch
            }

            val userCreated = getUserCreatedById(id)
            if (userCreated != null){
                _foodLoadState.value = FoodLoadState.Success(userCreated)
                return@launch
            }

            try {
                val apiFood = getUsdaFoodById(id)
                _foodLoadState.value = FoodLoadState.Success(apiFood)
            } catch (e: Exception) {
                _foodLoadState.value = FoodLoadState.Error(
                    "Unable to load food. You may be offline, and this item is not saved in your history."
                )
            }
        }

    }


    fun searchFoodByName(input: String) {
        query.value = input
    }
//
//    suspend fun getFoodServingSize(foodId: Long): Float {
//        return repo.getFoodServingSize(foodId)
//    }
//
//    suspend fun getFoodById(foodId: Long): Food{
//        return repo.getFoodById(foodId)
//    }

    var logFoodDate by mutableStateOf(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    )
    private set

    fun updateLogFoodDate(newDate: String){
        logFoodDate = newDate
    }

    fun insertLoggedFood(
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
        viewModelScope.launch{
            repo.insertLoggedFood(foodId, name, Date, category, weight, servingSize, kcal, carbs, protein, fat)
        }
    }

    fun deleteFromCache(id: Long){
        viewModelScope.launch {
            repo.deleteFromCache(id)
        }
    }

    var dishes by mutableStateOf(listOf<logged_foods>())
    private set

    var dailyMacros by mutableStateOf(DailyMacros(0F, 0F, 0F, 0F))
        private set

    fun List<logged_foods>.calculateTotals(): DailyMacros {
        return DailyMacros (
            cal = sumOf { it.kcal.toDouble() }.toFloat(),
            carb = sumOf { it.carbs.toDouble() }.toFloat(),
            protein = sumOf { it.protein.toDouble() }.toFloat(),
            fat = sumOf { it.fat.toDouble() }.toFloat()
        )

    }

    fun getLoggedFoodsByDate(date: String){
        viewModelScope.launch{
            dishes = repo.getLoggedFoodsByDate(date)
//            dailyMacros = repo.getDailyMacros(date)
            dailyMacros = dishes.calculateTotals()
        }
    }




    suspend fun getLoggedFoodsById(id: Long): logged_foods{
        return repo.getLoggedFoodsById(id)
    }

    fun updateLoggedFood(
        weight: Float,
        category: String,
        ss: String,
        kcal: Float,
        id: Long,
        carbs: Float,
        protein: Float,
        fat: Float){
        viewModelScope.launch {
            repo.updateLoggedFood(weight, category, ss, kcal, id, carbs, protein, fat)
        }
    }

    fun deleteLoggedFood(id: Long){
        viewModelScope.launch{
            repo.deleteLoggedFood(id)
        }
    }




    fun insertCachedFood(cachedFood: CachedFoods){
        viewModelScope.launch{
            repo.insertCacheFood(cachedFood)
        }
    }

    fun updateCacheTouch(cacheId: Long){
        viewModelScope.launch{
            repo.updateCacheTouch(cacheId)
        }
    }

    fun insertWithEviction(cachedFood: CachedFoods){
        viewModelScope.launch{
            repo.insertWithEviction(cachedFood)
        }
    }

    fun insertUserCreatedFood(newData: newFoodData){
        viewModelScope.launch{
            val USF = UserCreatedFoods(
                description = newData.description,
                brandName = newData.brandName,
                servingSize = newData.servingSize.toFloatOrNull() ?: 0f,
                protein = newData.protein.toFloatOrNull() ?: 0f,
                totalLipid = newData.totalLipid.toFloatOrNull() ?: 0f,
                carbs = newData.carbs.toFloatOrNull() ?: 0f,
                kcal = newData.kcal.toFloatOrNull() ?: 0f,
                sugars = newData.sugars.toFloatOrNull() ?: 0f,
                fiber = newData.fiber.toFloatOrNull() ?: 0f,
                calcium = newData.calcium.toFloatOrNull() ?: 0f,
                iron = newData.iron.toFloatOrNull() ?: 0f,
                potassium = newData.potassium.toFloatOrNull() ?: 0f,
                sodium = newData.sodium.toFloatOrNull() ?: 0f,
                vitaminA = newData.vitaminA.toFloatOrNull() ?: 0f,
                vitaminC = newData.vitaminC.toFloatOrNull() ?: 0f,
                thiamin = newData.thiamin.toFloatOrNull() ?: 0f,
                niacin = newData.niacin.toFloatOrNull() ?: 0f,
                folate = newData.folate.toFloatOrNull() ?: 0f,
                cholesterol = newData.cholesterol.toFloatOrNull() ?: 0f,
                transFat = newData.transFat.toFloatOrNull() ?: 0f,
                saturatedFat = newData.saturatedFat.toFloatOrNull() ?: 0f
            )
            repo.insertUserCreatedFood(USF)
        }
    }

}

sealed interface FoodLoadState {
    data object Loading : FoodLoadState
    data class Success(val food: UsdaAbridgedFood) : FoodLoadState
    data class Error(val message: String) : FoodLoadState
}