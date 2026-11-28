package com.example.test.data.download

import com.example.test.data.ExerciseDao
import com.example.test.data.NutrtionData.NutritionDAO

class HomeRepository(
    private val foodDao:  NutritionDAO,
    private val exerciseDao: ExerciseDao
) {

}