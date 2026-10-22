package com.example.test.data.download

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.Exercise
import com.example.test.data.ExerciseRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ExerciseRepo(application)

//    init {
//        viewModelScope.launch {
//            repo.refreshExercisesFromNetwork()
////            repo.getAllExercises()
//        }
//    }

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