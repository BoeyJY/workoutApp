package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

            composable("FoodLog") {
                FoodLog()
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