package com.example.test.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.test.data.ExerciseCardData
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)


    //
    @Query("SELECT EXISTS(SELECT * FROM exercises LIMIT 1)")
    suspend fun dbReady(): Boolean
    //

    @Query("SELECT * FROM exercises WHERE primaryMuscles LIKE '%' || :muscle || '%' ORDER BY id ASC")
    fun getExercisesPaged(muscle: String): PagingSource<Int, Exercise>

    @Query("SELECT DISTINCT primaryMuscles FROM exercises")
    fun getExercisesMuscles(): Flow<List<String>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getExerciseByID(id: String): Flow<Exercise>

    @Query("SELECT * FROM exercises WHERE name = :name")
    suspend fun getExerciseByName(name: String): Exercise


    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :input || '%' AND primaryMuscles LIKE '%' || :muscle || '%' ORDER BY name")
    fun searchExerciseByNameMuscle(input: String, muscle: String): Flow<List<Exercise>>

    @Query("SELECT * FROM workoutSessions WHERE date = :date LIMIT 1")
    suspend fun getSessionByDate(date: String): workoutSessions?

    @Query("SELECT * FROM sessionExercises WHERE sessionId = :sessionId AND exerciseId = :exerciseId LIMIT 1")
    suspend fun getSessionExercise(sessionId: Long, exerciseId: String): sessionExercises?

    @Query("SELECT * FROM " +
            "sessionSets ss INNER JOIN sessionExercises se ON ss.sessionExerciseId = se.id " +
            "INNER JOIN workoutSessions ws ON se.sessionId = ws.id " +
            "INNER JOIN exercises e ON se.exerciseId = e.id " +
            "WHERE ws.date = :date and e.name = :exerciseName")
    fun getSessionSet(date: String, exerciseName: String): Flow<List<sessionSets>>

    @Query("SELECT e.name, ss.comment, ss.weight, ss.reps FROM " +
            "sessionSets ss INNER JOIN sessionExercises se ON ss.sessionExerciseId = se.id " +
            "INNER JOIN workoutSessions ws on se.sessionId = ws.id " +
            "INNER JOIN exercises e ON e.id = se.exerciseId " +
            "WHERE ws.date = :date " +
            "ORDER BY se.orderIndex ASC, ss.setNumber ASC")
    suspend fun getSessionExercises(date: String): List<dailyExerciseCard>
    @Query("SELECT COALESCE(MAX(setNumber), 0) FROM sessionSets WHERE sessionExerciseId = :workoutExerciseId")
    suspend fun getMaxSetNumber(workoutExerciseId: Long): Int

    @Query("UPDATE sessionSets " +
            "SET weight = :weight, reps = :reps " +
            "WHERE id = :id")
    suspend fun updateSessionSet(id: Long, weight: Float, reps: Int)

    @Query("UPDATE sessionSets " +
            "SET comment = :comment " +
            "WHERE id = :id")
    suspend fun updateSessionSetComment(id:Long, comment: String)
    @Query("DELETE FROM sessionSets WHERE id = :id")
    suspend fun deleteSessionSet(id: Long)
    @Insert
    suspend fun insertSession(session: workoutSessions): Long // Returns the auto-generated ID

    @Insert
    suspend fun insertSessionExercise(sessionExercises: sessionExercises): Long // Returns ID

    @Insert
    suspend fun insertSet(sets: sessionSets) // Inserts all the sets at once

    @Transaction // Ensures the two queries below run together safely
    @Query("""
        SELECT se.*, e.name as exerciseName 
        FROM sessionExercises se
        JOIN exercises e ON se.exerciseId = e.id
        WHERE se.sessionId = :sessionId
        ORDER BY se.orderIndex ASC
    """)
    suspend fun getExerciseCardsForSession(sessionId: Long): List<ExerciseCardData>

    @Query("SELECT * FROM exercises ORDER BY name ASC")
    suspend fun getAllExercises(): List<Exercise>

    @Query("DELETE FROM exercises WHERE isUserCreated = 1")
    suspend fun deleteAllUserCreated()   // optional, if you want to clear them
}