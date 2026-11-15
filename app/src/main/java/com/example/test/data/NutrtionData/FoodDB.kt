package com.example.test.data.NutrtionData

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import java.io.File

@Database(entities =
[   Food::class,
    food_aliases::class,
    food_barcodes::class,
    food_servings::class,
    CachedFoods::class,
    logged_foods::class
], version = 4, exportSchema = false)

abstract class FoodDB : RoomDatabase(){
    abstract fun NutritionDAO() : NutritionDAO

    companion object {
        @Volatile
        private var INSTANCE: FoodDB? = null

fun getInstance(context: Context): FoodDB =
    return INSTANCE?: synchronized(this){
//                val dbFile = context.getDatabasePath("lethio-food-db.sqlite")
        val instance = Room.databaseBuilder(
            context.applicationContext,
            FoodDB::class.java,
            "lethio_db" // The name of the Room database
        )
//            .fallbackToDestructiveMigration(true)
            .createFromAsset("lethio_db.sqlite")
            .build()
        INSTANCE = instance
        instance

//                INSTANCE = instance
//                instance
    }

    }
}