package com.example.test.data.NutrtionData

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File

@Database(entities =
[
//    Food::class,
//    food_aliases::class,
//    food_barcodes::class,
//    food_servings::class,
    CachedFoods::class,
    logged_foods::class,
    UserCreatedFoods::class
], version = 6, exportSchema = false)

abstract class FoodDB : RoomDatabase(){
    abstract fun NutritionDAO() : NutritionDAO

    companion object {
        @Volatile
        private var INSTANCE: FoodDB? = null

fun getInstance(context: Context): FoodDB =
    return INSTANCE?: synchronized(this){
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `UserCreatedFoods` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `description` TEXT NOT NULL,
                `servingSize` REAL NOT NULL,
                `protein` REAL NOT NULL,
                `totalLipid` REAL NOT NULL,
                `carbs` REAL NOT NULL,
                `kcal` REAL NOT NULL,
                `sugars` REAL NOT NULL,
                `fiber` REAL NOT NULL,
                `calcium` REAL NOT NULL,
                `iron` REAL NOT NULL,
                `potassium` REAL NOT NULL,
                `sodium` REAL NOT NULL,
                `vitaminA` REAL NOT NULL,
                `vitaminC` REAL NOT NULL,
                `thiamin` REAL NOT NULL,
                `niacin` REAL NOT NULL,
                `folate` REAL NOT NULL,
                `cholesterol` REAL NOT NULL,
                `transFat` REAL NOT NULL,
                `saturatedFat` REAL NOT NULL,
                `lastUsed` INTEGER NOT NULL
            )
            """.trimIndent()
                )
                db.execSQL(
                    """
            CREATE UNIQUE INDEX IF NOT EXISTS 
            `index_UserCreatedFoods_description`
            ON `UserCreatedFoods` (`description`)
            """.trimIndent()
                )
            }
        }
//                val dbFile = context.getDatabasePath("lethio-food-db.sqlite")
        val instance = Room.databaseBuilder(
            context.applicationContext,
            FoodDB::class.java,
            "nutrition" // The name of the Room database
        )
//            .fallbackToDestructiveMigration(true)
//            .createFromAsset("lethio_db.sqlite")
            .addMigrations(MIGRATION_5_6)
            .build()
        INSTANCE = instance
        instance

//                INSTANCE = instance
//                instance
    }

    }
}