package com.example.test.data.NutrtionData


import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
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

class NutritionViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = NutritionRepo(application)

//    private val _dbState = MutableStateFlow<DbState>(DbState.Checking)
//    val dbState: StateFlow<DbState> = _dbState.asStateFlow()

//    init {
//        viewModelScope.launch {
////            repo.refreshFoodFromNetwork()
//            try{
//
//            }
//        }
//
//    }

    private val query = MutableStateFlow("")

    val searchQuery: StateFlow<String> = query

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: StateFlow<List<Food>> =
//        combine(_dbState,query
//            .debounce(500.milliseconds)) { state, q -> state to q.trim()}
//            .distinctUntilChanged()
//            .flatMapLatest { (state, q) ->
//                if (state !is DbState.Ready || q.length <2){
//                    flowOf(emptyList())
//                }
//                else {
//                    repo.searchFoodByName(q)
//                }
//            }
//            .stateIn(
//                viewModelScope,
//                SharingStarted.WhileSubscribed(5000),
//                emptyList()
//            )
        query
        .debounce(500.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repo.searchFoodByName(query)
        }
        .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
        )
//
//    init { initDatabase() }
//
//    private fun initDatabase() {
//        viewModelScope.launch {
//            _dbState.value = DbState.Checking
//            try {
//                if (repo.isDatabaseReady()) {
//                    _dbState.value = DbState.Ready
//                    return@launch
//                }
//                _dbState.value = DbState.Downloading(-1f)
//                repo.ensureReady { p -> _dbState.value = DbState.Downloading(p) }
//                _dbState.value = DbState.Ready
//            } catch (t: Throwable) {
//                Log.e("NutritionVM", "DB init failed", t)
//                _dbState.value = DbState.Error(t.message ?: "Failed to load database")
//            }
//        }
//    }
//
//    fun retry() = initDatabase()

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