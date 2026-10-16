package com.example.test

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.NavController


@Composable
fun BotNavBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar {
        val items = listOf(
            NavigationItem(
                route = "HomePage",
                icon = "⌂",
                label = "Home",
                iconSize = 24.sp
            ),
            NavigationItem(
                route = "LogWorkout",
                icon = "●",
                label = "Workouts",
                iconSize = 22.sp
            ),
            NavigationItem(
                route = "FoodLog",
                icon = "⚙",
                label = "Nutrition",
                iconSize = 20.sp
            )
        )

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination to avoid stacking
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Text(
                        text = item.icon,
                        fontSize = item.iconSize
                    )
                },
                label = {
                    Text(item.label)
                }
            )
        }
    }
}

data class NavigationItem(
    val route: String,
    val icon: String,
    val label: String,
    val iconSize: TextUnit
)