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
import com.example.test.data.Exercise
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
class NutritionViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = NutritionRepo(application,
        apiKey = "")



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

//
//    private val _uiState =
//        MutableStateFlow<FoodUiState>(FoodUiState.Idle)
//
//    val uiState: StateFlow<FoodUiState> =
//        _uiState.asStateFlow()




    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<UsdaFood>> =
        query
        .debounce(500.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(PagingData.empty())
                } else {
                    repo.searchPaginated(query.trim())
                }

        }.cachedIn(viewModelScope)
//        .stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(5000),
//        emptyList()
//        )

    fun searchFoodByName(input: String) {
        query.value = input
    }

    suspend fun getFoodServingSize(foodId: Long): Float {
        return repo.getFoodServingSize(foodId)
    }

    suspend fun getFoodById(foodId: Long): Food{
        return repo.getFoodById(foodId)
    }

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
        kcal: Float
    ){
        viewModelScope.launch{
            repo.insertLoggedFood(foodId, name, Date, category, weight, servingSize, kcal)
        }
    }


    var dishes by mutableStateOf(listOf<logged_foods>())
    private set

    var dailyMacros by mutableStateOf(DailyMacros(0F, 0F, 0F, 0F))
        private set

    fun getLoggedFoodsByDate(date: String){
        viewModelScope.launch{
            dishes = repo.getLoggedFoodsByDate(date)
            dailyMacros = repo.getDailyMacros(date)
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
        id: Long){
        viewModelScope.launch {
            repo.updateLoggedFood(weight, category, ss, kcal, id)
        }
    }

    fun deleteLoggedFood(id: Long){
        viewModelScope.launch{
            repo.deleteLoggedFood(id)
        }
    }

}
//
//sealed interface DbState {
//    /** Checking whether the file exists — usually a few ms. */
//    data object Checking : DbState
//    /** Download in flight. progress in 0f..1f, or -1f when the size is unknown. */
//    data class Downloading(val progress: Float) : DbState
//    /** DB is on disk, Room can be opened. This is the only state in which search works. */
//    data object Ready : DbState
//    data class Error(val message: String) : DbState
//}