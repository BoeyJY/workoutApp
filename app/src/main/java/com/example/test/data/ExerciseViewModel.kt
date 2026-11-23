package com.example.test.data


import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ExerciseRepo(application)

    private val _muscleFilter = MutableStateFlow("")
    val muscleFilter: StateFlow<String> = _muscleFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val exercisesPaged: Flow<PagingData<Exercise>> = _muscleFilter
        .flatMapLatest { muscle -> repository.getExercisesPaged(muscle).cachedIn(viewModelScope) }



    fun setMuscleFilter(muscle: String) {
        _muscleFilter.value = muscle
    }

    private val _muscleList = MutableStateFlow<List<String>>(emptyList())
    val muscleList = _muscleList.asStateFlow()




    // UI state: loading, list of exercises, error message, etc.
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

//    init {
//        Log.d("test", "ViewModel")
//        // On ViewModel creation, refresh from network (if needed) and load from DB.
//        viewModelScope.launch {
//            Log.d("test test", "ViewModel")
//            // First, try to refresh (this will only hit network if DB is empty)
//            repository.refreshExercisesFromNetwork()
//
//            // Then collect the database flow and update UI state
//            repository.getAllExercises().collect { exercises ->
//                _uiState.value = UiState.Success(exercises)
//            }
//        }
//    }
    init {
        viewModelScope.launch {
            repository.getExerciseMuscles().collect { _muscleList.value = it }
        }
    }

    fun addUserExercise(
        name: String,
        force: String?,
        level: String?,
        mechanic: String?,
        equipment: String?,
        instructions: String? = null,
        category: String,
        primaryMuscles: String,
        secondaryMuscles: List<String>?
    ) {
        viewModelScope.launch {
            var second = ""
            if(!secondaryMuscles.isNullOrEmpty()){
                secondaryMuscles.forEach { muscle ->
                    second = "$second$muscle,"
                }
                second = second.dropLast(1)
            }
            val newExercise = Exercise(
                id = "user_${System.currentTimeMillis()}", // generate unique ID
                name = name,
                instructions = instructions,
                isUserCreated = true,
                force = if(force.isNullOrBlank()){null}else{force},
                level = if(level.isNullOrBlank()){null}else{level},
                mechanic = if(mechanic.isNullOrBlank()){null}else{mechanic},
                equipment = if(equipment.isNullOrBlank()){null}else{equipment},
                category = category,
                primaryMuscles = primaryMuscles,
                secondaryMuscles = if(second.isNullOrBlank()){null}else{second}
            )
            repository.addUserExercise(newExercise)
        }
    }

    private val musclesDeferred: Deferred<List<String>> = viewModelScope.async {
        repository.getExerciseColumn("primaryMuscles")
    }

    suspend fun getDistinctMuscle(): List<String> = musclesDeferred.await()

    private val exerciseForce: Deferred<List<String>> = viewModelScope.async {
        repository.getExerciseColumn("force")
    }

    suspend fun getExerciseForce(): List<String> = exerciseForce.await()

    private val exerciseLevel: Deferred<List<String>> = viewModelScope.async {
        repository.getExerciseColumn("level")
    }

    suspend fun getExerciseLevel(): List<String> = exerciseLevel.await()

    private val exerciseMechanic: Deferred<List<String>> = viewModelScope.async {
        repository.getExerciseColumn("mechanic")
    }

    suspend fun getExerciseMechanic(): List<String> = exerciseMechanic.await()


    private val exerciseEquipment: Deferred<List<String>> = viewModelScope.async {
        repository.getExerciseColumn("equipment")
    }

    suspend fun getExerciseEquipment(): List<String> = exerciseEquipment.await()

    private val exerciseCategory: Deferred<List<String>> = viewModelScope.async {
        repository.getExerciseColumn("category")
    }

    suspend fun getExerciseCategory(): List<String> = exerciseCategory.await()


    fun getExerciseByID(ID: String): Flow<Exercise>{
        return repository.getExerciseByID(ID)
    }






    fun addSet(date: String, exerciseName: String, comment: String? = null, weight: Float, reps: Int){
        viewModelScope.launch{
            repository.saveSessionExercises(date, exerciseName, comment, weight, reps)
        }
    }


    var isCopying by mutableStateOf(false)
        private set
     fun copyPreviousWorkout(items: List<(dailyExerciseCard)>,
                             navDate: String,
                             onComplete: () -> Unit){
        viewModelScope.launch {

            isCopying = true
            try {
                items.forEach { set ->
                    repository.saveSessionExercises(
                        navDate,
                        exerciseName = set.name,
                        comment = set.comment,
                        weight = set.weight,
                        reps = set.reps
                    )
                }
                onComplete()
            } finally{
                isCopying = false
            }
        }
    }



    fun getSessionSets(date: String, exerciseName: String):Flow<List<sessionSets>> {
//        viewModelScope.launch {
//            sets = repository.getSessionSets(date, exerciseName)
//        }
        return repository.getSessionSets(date, exerciseName)
    }

    var sets by mutableStateOf(listOf<dailyExerciseCard>())
        private set

    fun getSessionExercises(date: String){
        viewModelScope.launch {
            sets = repository.getSessionExercises(date)
        }
    }

    private val query = MutableStateFlow("")

    val searchQuery: StateFlow<String> = query

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: StateFlow<List<Exercise>> =
//        query
        combine(
            query.debounce(500.milliseconds)
            .distinctUntilChanged(),
            _muscleFilter){ query, muscle -> query to muscle}
            .distinctUntilChanged()
            .flatMapLatest { (query, muscle) ->
                repository.searchExerciseByNameMuscle(query, muscle)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun searchExerciseByName(input: String) {
        query.value = input
    }

    fun updateSessionSet(id: Long, weight: Float, reps: Int){
        viewModelScope.launch {
            repository.updateSessionSet(id = id, weight = weight, reps = reps)
        }
    }

    fun updateSessionSetComment(id: Long, comment: String){
        viewModelScope.launch {
            repository.updateSessionSetComment(id = id, comment = comment)
        }
    }

    fun deleteSessionSet(id: Long){
        viewModelScope.launch {
            repository.deleteSessionSte(id = id)
        }
    }

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate.asStateFlow()

    // 2. Function to handle the day click
    fun calendarClicked(date: String) {
        _selectedDate.value = date
        // Trigger your existing query logic
        getSessionExercises(date)
    }

    var logWorkoutDate by mutableStateOf(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    )
        private set

    // 2. Add a function to update it
    fun updateLogWorkoutDate(newDate: String) {
        logWorkoutDate = newDate
    }

    data class GraphUiState(
        val isLoading: Boolean = true,
        val data: List<MaxWeightPerDay> = emptyList(),
        val error: String? = null
    )

    private val _graphUiState = MutableStateFlow(GraphUiState())
    val graphUiState: StateFlow<GraphUiState> =
        _graphUiState.asStateFlow()

    fun getMaxWeightPerDay(exId: String) {
        viewModelScope.launch {
            _graphUiState.value = GraphUiState(isLoading = true)
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getMaxWeightPerDay(exId)
                }
                _graphUiState.value = GraphUiState(
                    isLoading = false,
                    data = result
                )
            } catch (e: Exception) {
                _graphUiState.value = GraphUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load workout data"
                )
            }
        }
    }

    suspend fun getExerciseIdByName(name: String): String =
        repository.getExerciseIdByName(name)


    sealed class UiState {
        object Loading : UiState()
        data class Success(val exercises: List<Exercise>) : UiState()
        data class Error(val message: String) : UiState()
    }
}