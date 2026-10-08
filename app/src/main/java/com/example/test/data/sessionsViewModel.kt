package com.example.test.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: ExerciseDao = ExerciseDB.getInstance(application).exerciseDao()
    private val repo = sessionsRepo(dao)

    suspend fun addSet(date: String, exerciseName: String, weight: Float, reps: Int){
        repo.saveSessionExercises(date, exerciseName, weight, reps)
    }
}
