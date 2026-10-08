package com.example.test.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities =
    [Exercise::class,
    workoutSessions::class,
    sessionExercises::class,
    sessionSets::class
    ], version = 5, exportSchema = false)
abstract class ExerciseDB : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: ExerciseDB? = null

        fun getInstance(context: Context): ExerciseDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                ExerciseDB::class.java,
                                "exercise_database"
                            ).fallbackToDestructiveMigration(true)
                    //TODO remove the fallback and write a migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}