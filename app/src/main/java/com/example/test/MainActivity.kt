package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.test.nutrition.FoodAdd
import com.example.test.nutrition.FoodList
import com.example.test.nutrition.FoodLog
import com.example.test.nutrition.UserFoodAdd
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

    val SCREEN_TRANSITION_MILLIS = 500

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
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            enterTransition = { slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(
                    SCREEN_TRANSITION_MILLIS
                )
            ) },
            exitTransition = { slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(
                    SCREEN_TRANSITION_MILLIS
                )
            ) },
            popEnterTransition = { slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(
                    SCREEN_TRANSITION_MILLIS
                )
            ) },
            popExitTransition = { slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(
                    SCREEN_TRANSITION_MILLIS
                )
            ) }
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

            composable ("FoodAdd/{date}/{foodId}/{category}?servingSize={servingSize}&brand={brand}&loggedId={loggedId}",
                arguments = listOf(navArgument("date") { defaultValue = "" },
                        navArgument("foodId") { defaultValue = "" },
                    navArgument("category") { defaultValue = "Other" },
                    navArgument("servingSize") {
                        type = NavType.FloatType
                        defaultValue = 100F },
                    navArgument("brand") {
                        nullable = true
                        defaultValue = null },
                    navArgument("loggedId") {
                        nullable = true
                        defaultValue = null }
                    ))
            {
                backStackEntry ->
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
                val category = backStackEntry.arguments?.getString("category") ?: ""
                val servingSize = backStackEntry.arguments?.getFloat("servingSize")?: 100F
                val brand = backStackEntry.arguments?.getString("brand")
                val loggedId = backStackEntry.arguments?.getString("loggedId")
                FoodAdd(date = date,
                    foodId = foodId,
                    category = category,
                    servingSize = servingSize,
                    brand = brand,
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
                route = "ExerciseGraph/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { defaultValue = "" })
            ) {
                backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                ExerciseGraph(exerciseId = exerciseId, navController = navController)
            }


            composable(
                route = "CalendarScreen?navDate={navDate}&copy={copy}",
                arguments = listOf(navArgument("navDate") {
                    nullable = true
                    defaultValue = null },
                    navArgument("copy") {
                        type = NavType.BoolType
                        defaultValue = false })
            ){
                backStackEntry ->
                val navDate = backStackEntry.arguments?.getString("navDate")
                val copy = backStackEntry.arguments?.getBoolean("copy")?: false
                CalendarScreen(navController = navController, navDate = navDate, copy = copy)
            }

            composable(
                route = "UserExerciseAdd"
            ){
                UserExerciseAdd(navController = navController)
            }

            composable(
                route = "UserFoodAdd"
            ){
                UserFoodAdd(navController = navController)
            }
        }
    }
}