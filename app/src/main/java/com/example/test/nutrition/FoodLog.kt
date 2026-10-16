package com.example.test.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
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

@Composable
fun FoodLog(viewModel: NutritionViewModel = viewModel(),
            navController: NavController) {

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

            // -------------------------
            // Macro section
            // -------------------------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Today's Macros",
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {

                Button(
                    onClick = {
                        navController.navigate("FoodList")
                    },
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape
                ) {
                    Text(
                        text = "+",
                        fontSize = 32.sp
                    )
                }
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