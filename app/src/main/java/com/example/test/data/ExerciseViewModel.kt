package com.example.test.data


import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
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

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ExerciseRepo(application)

    val exercisesPaged: Flow<PagingData<Exercise>> = repository.getExercisesPaged()

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

    fun addUserExercise(name: String, instructions: String?) {
        viewModelScope.launch {
            val newExercise = Exercise(
                id = "user_${System.currentTimeMillis()}", // generate unique ID
                name = name,
                instructions = instructions,
                isUserCreated = true,
                force = TODO(),
                level = TODO(),
                mechanic = TODO(),
                equipment = TODO(),
                category = TODO(),
                primaryMuscles = TODO(),
                secondaryMuscles = TODO(),
                imagePath = TODO(),
                createdAt = TODO()
            )
            repository.addUserExercise(newExercise)
        }
    }

    fun getExerciseByID(ID: String): Flow<Exercise>{
        return repository.getExerciseByID(ID)
    }


    fun addSet(date: String, exerciseName: String, comment: String? = null, weight: Float, reps: Int){
        viewModelScope.launch{
            repository.saveSessionExercises(date, exerciseName, comment, weight, reps)
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
        query
            .debounce(500.milliseconds)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                repository.searchExerciseByName(query)
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

    sealed class UiState {
        object Loading : UiState()
        data class Success(val exercises: List<Exercise>) : UiState()
        data class Error(val message: String) : UiState()
    }
}