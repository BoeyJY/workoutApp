package com.example.test.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities =
    [Exercise::class,
    workoutSessions::class,
    sessionExercises::class,
    sessionSets::class
    ], version = 6, exportSchema = false)
abstract class ExerciseDB : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: ExerciseDB? = null

        fun getInstance(context: Context): ExerciseDB {
            return INSTANCE ?: synchronized(this) {
                val MIGRATION_5_6 = object : Migration(5, 6) {
                    override fun migrate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            DELETE FROM exercises
                            WHERE id NOT IN (
                                SELECT MIN(id)
                                FROM exercises
                                GROUP BY name
                            )
                            """.trimIndent()
                        )
                        db.execSQL(
                            """
                            CREATE UNIQUE INDEX index_exercises_name
                            ON exercises(name)
                            """.trimIndent()
                        )
                    }
                }
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                ExerciseDB::class.java,
                                "exercise_database"
                            )
                    .addMigrations(MIGRATION_5_6)
//                    .fallbackToDestructiveMigration(true)
                    //TODO remove the fallback and write a migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}