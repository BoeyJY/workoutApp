package com.example.test.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.test.data.ExerciseViewModel
//import com.example.test.data.NutrtionData.DbState
import com.example.test.data.NutrtionData.NutritionViewModel
import com.example.test.topBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun FoodLog(viewModel: NutritionViewModel = viewModel(),
            navDate: String? = null,
            navController: NavController) {

    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]

    val selectedDate = viewModel.logFoodDate


    var lastAppliedNavDate by rememberSaveable { mutableStateOf<String?>(null) }
    val dishes = viewModel.dishes
    LaunchedEffect(navDate) {
        if (navDate != null && navDate != selectedDate && navDate != lastAppliedNavDate) {
            println("log Food navDate not working")
            viewModel.updateLogFoodDate(navDate)
            lastAppliedNavDate = navDate
        }
    }

    LaunchedEffect(selectedDate) {
        viewModel.getLoggedFoodsByDate(selectedDate)
    }
    val cards = dishes.groupBy { it.category }

    val date = remember(selectedDate) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        Calendar.getInstance().apply {
            time = sdf.parse(selectedDate) ?: Date()
        }
    }

    Scaffold(
        topBar = {
            topBar("Nutrition")
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            val newCal = (date.clone() as Calendar).apply {
                                add(Calendar.DAY_OF_MONTH, -1)
                            }
                            val newDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(newCal.time)
                            viewModel.updateLogFoodDate(newDateStr)
                        }, modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous day"
                        )
                    }

                    Text(
                        text = SimpleDateFormat(
                            "EEEE, MMM d",
                            locale
                        ).format(date.time),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            val newCal = (date.clone() as Calendar).apply {
                                add(Calendar.DAY_OF_MONTH, 1)
                            }
                            val newDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(newCal.time)
                            viewModel.updateLogFoodDate(newDateStr)
                        }, modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next day"
                        )
                    }
                }
            }
            // -------------------------
            // Macro section
            // -------------------------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Macros",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // Protein
                MacroBar(
                    name = "Protein",
                    current = 120,
                    goal = 160,
                    color = Color(0xFF4CAF50)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // Carbohydrates
                MacroBar(
                    name = "Carbs",
                    current = 180,
                    goal = 250,
                    color = Color(0xFFFF9800)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // Fat
                MacroBar(
                    name = "Fat",
                    current = 60,
                    goal = 80,
                    color = Color(0xFF2196F3)
                )
            }

            // -------------------------
            // Food logging section
            // -------------------------
            val meals = listOf("Breakfast", "Lunch", "Dinner", "Other")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    ) {
                    val date = SimpleDateFormat(
                        "yyyy-MM-dd"
                    ).format(date.time)
                    meals.forEach{ meal ->
                        item(key = meal) {
                            Category(date, meal, navController)
                        }
                        val dishesInMeal = cards[meal].orEmpty()
                        if (dishesInMeal.isNotEmpty()){
                            items(dishesInMeal.toList()){ dish ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RectangleShape   // no rounded corners so seams are clean
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    ) {
                                        Text(dish.name,style = MaterialTheme.typography.titleMedium)

                                        Text("${dish.weight}g  ${dish.kcal}kcal",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        val date = SimpleDateFormat(
                            "yyyy-MM-dd"
                        ).format(date.time)
                        navController.navigate("FoodList/$date")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(64.dp),
                    shape = CircleShape
                ) {
                    Text(
                        text = "+",
                        fontSize = 28.sp
                    )
                }
            }
        }
    }
}

@Composable
fun Category(
    date: String,
    title: String,
    navController: NavController
){
    Surface(
        modifier = Modifier.fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            ),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = {
                        //TODO
                        navController.navigate("FoodList/${date}?category=$title")
                 }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add to $title")
            }
        }
    }
}

@Composable
fun MacroBar(
    name: String,
    current: Int,
    goal: Int,
    color: Color
) {
    val barColor = if(current > goal){Color(0xFFFF000D)}
    else {color}

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = name,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "$current / ${goal}g"
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        LinearProgressIndicator(
            progress = {
                (current.toFloat() / goal.toFloat())
                    .coerceIn(0f, 1f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}


@Preview(showBackground = true)
@Composable
fun FoodLogPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        FoodLog(navController = navController)
    }
}