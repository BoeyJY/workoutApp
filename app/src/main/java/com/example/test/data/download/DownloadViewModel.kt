package com.example.test.data.download

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.BuildConfig
import com.example.test.data.Exercise
import com.example.test.data.ExerciseRepo
import com.example.test.data.NutrtionData.DailyMacros
import com.example.test.data.NutrtionData.NutritionRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ExerciseRepo(application)
    private val nutritionRepo = NutritionRepo(application, BuildConfig.USDA_APIKEY)

//    init {
//        viewModelScope.launch {
//            repo.refreshExercisesFromNetwork()
////            repo.getAllExercises()
//        }
//    }

    private val _selectedDate = MutableStateFlow(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        .format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val dailyMacros: StateFlow<DailyMacros> = _selectedDate
        .flatMapLatest { date -> nutritionRepo.getDailyMacrosFlow(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DailyMacros(0f, 0f, 0f, 0f)
        )

    fun setDate(newDate: String) { _selectedDate.value = newDate }

    sealed interface DownloadState {
        data object NotStarted : DownloadState
        data object Downloading : DownloadState
        data object Success : DownloadState
        data class Error(val message: String) : DownloadState
    }

    private val _downloadState =
        MutableStateFlow<DownloadState>(DownloadState.NotStarted)

    val downloadState: StateFlow<DownloadState> =
        _downloadState.asStateFlow()
    fun refreshExercisesFromNetwork() {
        viewModelScope.launch {
            _downloadState.value = DownloadState.Downloading
            try {
                val success = repo.refreshExercisesFromNetwork()

                _downloadState.value =
                    if (success) {
                        DownloadState.Success
                    } else {
                        DownloadState.Error(
                            "Download failed. Please try again."
                        )
                    }
            } catch (e: Exception) {
                _downloadState.value = DownloadState.Error(
                    e.message ?: "An unexpected error occurred."
                )
            }

        }
    }



    var showDialog by mutableStateOf(false)
        private set
    var dbReady by mutableStateOf<Boolean?>(null)
        private set
    fun dbReady() {
        viewModelScope.launch {
            dbReady = repo.dbReady()
            println("dbready ${dbReady}")
            showDialog = !repo.dbReady()
        }
    }

    fun dismissDialog(){
        showDialog = false
    }

    //
    var ex = "testing"
     fun getExerciseByName() {
        viewModelScope.launch{
            val th = repo.getExerciseByName("Ab Crunch Machine")
            ex = th.id
        }
     }
    //
}