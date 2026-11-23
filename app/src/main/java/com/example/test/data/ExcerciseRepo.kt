package com.example.test.data


import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.test.network.NetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExerciseRepo(private val context: Context) {
    private val dao = ExerciseDB.getInstance(context).exerciseDao()

    suspend fun dbReady(): Boolean{
        return dao.dbReady()
    }

    fun getExercisesPaged(muscle: String): Flow<PagingData<Exercise>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,               // Load 20 items per page
                prefetchDistance = 5,        // Load the next page when 5 items away from the bottom
                enablePlaceholders = false   // Don't show placeholders for unsupported items
            ),
            pagingSourceFactory = { dao.getExercisesPaged(muscle) }
        ).flow
    }

    fun getExerciseMuscles(): Flow<List<String>>{
        return dao.getExercisesMuscles()
    }
    fun getExerciseByID(id:String): Flow<Exercise>{
        return dao.getExerciseByID(id)
    }
    // Flow that emits all exercises from Room
    fun getAllExercises(): Flow<List<Exercise>> = flow {
        emit(dao.getAllExercises())
    }

    // Fetch from GitHub and store in Room (should be called only once on first launch)
    suspend fun refreshExercisesFromNetwork(): Boolean =
        withContext(Dispatchers.IO) {
            // Check if we already have exercises in DB (to avoid redundant network calls)
            val existing = dao.getAllExercises()
            if (existing.isNotEmpty()) return@withContext true

            try {
                val response = NetworkModule.api.fetchExercises()
                val json = response.string()
                val exercises = ExerciseParser.parseExerciseJson(json)
                dao.insertAll(exercises)
            true
            } catch (e: Exception) {
                // Handle error (maybe log or fallback to bundled JSON)
//
                println("Execption caught in repo")
                e.printStackTrace()
                false
            }
        }


    // Add a user-created exercise
    suspend fun addUserExercise(exercise: Exercise) {
        withContext(Dispatchers.IO) {
            dao.insertExercise(exercise.copy(isUserCreated = true))
        }
    }

    // Optional: delete all user exercises
    suspend fun clearUserExercises() {
        withContext(Dispatchers.IO) {
            dao.deleteAllUserCreated()
        }
    }
    suspend fun saveSessionExercises(
        date: String,              // The date of Screen A
        exerciseName: String,        // The exercise picked from Screen B
        comment: String? = null,
        weight: Float, // List of (Weight, Reps) entered by user
        reps: Int
    ) {
        // 1. Get or Create the workoutSessions for this date
        var session = dao.getSessionByDate(date)
        if (session == null) {
            val sessionId = dao.insertSession(workoutSessions(date = date))
            session = workoutSessions(id = sessionId, date = date)
        }

        var exercise = dao.getExerciseByName(exerciseName)

        // 2. Create the WorkoutExercise (the card)
        var sessionExercise = dao.getSessionExercise(session.id, exercise.id)

        if (sessionExercise == null) {
            val cardId = dao.insertSessionExercise(
                sessionExercises(
                    sessionId = session.id,
                    exerciseId = exercise.id,
                    orderIndex = 0 // TODO query the database for the count to get the order
                )
            )
            // Fetch the full object (or just use the ID)
            sessionExercise = sessionExercises(id = cardId, sessionId = session.id, exerciseId = exercise.id, orderIndex = 0)
        }

        val nextSetNumber = dao.getMaxSetNumber(sessionExercise.id) + 1

        // 3. Insert all the sets for this exercise
        val setEntity = sessionSets(
            sessionExerciseId = sessionExercise.id,
            setNumber = nextSetNumber,
            comment = comment,
            weight = weight,
            reps = reps
        )
        dao.insertSet(setEntity)
    }



    fun getSessionSets(date: String, exerciseName: String): Flow<List<sessionSets>>{
//        var exercise = dao.getExerciseByName(exerciseName)
        return dao.getSessionSet(date = date, exerciseName = exerciseName)
    }

    suspend fun getSessionExercises(date: String): List<dailyExerciseCard>{
        return dao.getSessionExercises(date)
    }

    fun searchExerciseByNameMuscle(input: String, muscle: String): Flow<List<Exercise>>{
        return dao.searchExerciseByNameMuscle(input, muscle)
    }

    suspend fun updateSessionSet(id: Long, weight: Float, reps: Int){
        dao.updateSessionSet(id = id, weight = weight, reps = reps)
    }

    suspend fun updateSessionSetComment(id: Long, comment: String){
        dao.updateSessionSetComment(id = id, comment = comment)
    }

    suspend fun deleteSessionSte(id: Long){
        dao.deleteSessionSet(id = id)
    }


    // Called when Screen A loads or the date changes
    suspend fun getWorkoutForDate(date: String): List<ExerciseCardData> {
        // 1. Find the session for this date
        val session = dao.getSessionByDate(date)
        // 2. If no session, return empty list (no cards)
        return if (session == null) emptyList()
        else dao.getExerciseCardsForSession(session.id)
    }

    suspend fun getMaxWeightPerDay(exId: String): List<MaxWeightPerDay> {
        return dao.getMaxWeightPerDay(exId)
    }

    suspend fun getExerciseIdByName(name: String): String{
        return dao.getExerciseIdByName(name)
    }


    suspend fun getExerciseColumn(column: String): List<String>{
        val allowedColumns = setOf(
            "force",
            "level",
            "mechanic",
            "equipment",
            "category",
            "primaryMuscles"
        )
        require(column in allowedColumns)
        return dao.getExerciseColumn(
            SimpleSQLiteQuery(
                "SELECT DISTINCT $column FROM exercises WHERE $column IS NOT NULL"
            )
        )
    }


    // del later
    suspend fun getExerciseByName(name: String): Exercise{
        return dao.getExerciseByName(name)
    }
}
