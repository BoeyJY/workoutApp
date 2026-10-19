package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.test.nutrition.FoodAdd
import com.example.test.nutrition.FoodList
import com.example.test.nutrition.FoodLog
import java.net.URLDecoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BotNavBar(
                navController = navController,
                currentRoute = navController.currentBackStackEntry?.destination?.route
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "HomePage",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("HomePage") {
                HomePage()
            }
            composable("LogWorkout?navDate={navDate}",
                arguments = listOf(navArgument("navDate") {
                    nullable = true
                    defaultValue = null })
            )
            {
                backStackEntry ->
                val navDate = backStackEntry.arguments?.getString("navDate")
                LogWorkout(navController = navController, navDate = navDate)
            }

            composable("FoodLog?navDate={navDate}",
                arguments = listOf(navArgument("navDate") {
                nullable = true
                defaultValue = null })
            )
            {
                backStackEntry ->
                val navDate = backStackEntry.arguments?.getString("navDate")
                FoodLog(navController = navController, navDate = navDate)
            }

            composable("FoodList/{date}?category={category}",
                arguments = listOf(navArgument("date") { defaultValue = "" },
                    navArgument("category") {
                        nullable = false
                        defaultValue = "Other" }))
            {
                backStackEntry ->
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val category = backStackEntry.arguments?.getString("category")?: "Other"
                FoodList(date = date, category = category, navController = navController)
            }

            composable ("FoodAdd/{date}/{foodId}/{category}?loggedId={loggedId}",
                arguments = listOf(navArgument("date") { defaultValue = "" },
                        navArgument("foodId") { defaultValue = "" },
                    navArgument("category") { defaultValue = "Other" },
                    navArgument("loggedId") {
                        nullable = true
                        defaultValue = null }))
            {
                backStackEntry ->
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
                val category = backStackEntry.arguments?.getString("category") ?: ""
                val loggedId = backStackEntry.arguments?.getString("loggedId")
                FoodAdd(date = date,
                    foodId = foodId,
                    category = category,
                    loggedId = loggedId,
                    navController = navController)
            }

            composable(
                route = "ExerciseList/{date}",
                arguments = listOf(navArgument("date") { defaultValue = "" }))
            {
                backStackEntry ->
                val date = backStackEntry.arguments?.getString("date") ?: ""
                ExerciseList(date = date, navController = navController)
            }

            composable(
                route = "ExerciseDetails/{exerciseID}",
                arguments = listOf(navArgument("exerciseID") { defaultValue = "" })
            ) { backStackEntry ->
                val exerciseID = backStackEntry.arguments?.getString("exerciseID") ?: ""
                ExerciseDetails(exerciseID = exerciseID, navController = navController)
            }

            composable(
                route = "ExerciseAdd/{date}/{exerciseName}",
                arguments = listOf(
                    navArgument("date") { defaultValue = "" },
                    navArgument("exerciseName") { defaultValue = "" })
            ) { backStackEntry ->
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val encodedName = backStackEntry.arguments?.getString("exerciseName") ?: ""
                val exerciseName = URLDecoder.decode(encodedName, "UTF-8")
                ExerciseAdd(
                    date = date,
                    exerciseName = exerciseName,
                    navController = navController
                )
            }


            composable(
                route = "CalendarScreen"
            ) {
                CalendarScreen(navController = navController)
            }
        }
    }
}