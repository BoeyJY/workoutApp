package com.example.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.test.data.Exercise
import com.example.test.data.ExerciseViewModel

@Composable
fun ExerciseDetails(exerciseID: String,
                    viewModel: ExerciseViewModel = viewModel(),
                    navController: NavController
) {
    val fetchedExercise by viewModel.getExerciseByID(exerciseID).collectAsState(initial = null)

    val exercise = fetchedExercise
    Scaffold(
        topBar = {
            topBar(
                exercise?.name ?: "Error",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { Padding ->
        if (exercise == null){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No exercise found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = Padding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth())
                {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoItem(
                                modifier = Modifier.weight(1f),
                                label = "Force",
                                value = exercise.force
                            )

                            InfoItem(
                                modifier = Modifier.weight(1f),
                                label = "Level",
                                value = exercise.level
                            )

                            InfoItem(
                                modifier = Modifier.weight(1f),
                                label = "Mechanic",
                                value = exercise.mechanic
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoItem(
                                modifier = Modifier.weight(1f),
                                label = "Equipment",
                                value = exercise.equipment
                            )

                            Spacer(
                                modifier = Modifier.weight(0.5f)
                            )

                            InfoItem(
                                modifier = Modifier.weight(1f),
                                label = "Category",
                                value = exercise.category
                            )

                            // Keeps the two items from stretching too far apart

                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Muscles Worked",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        exercise.primaryMuscles?.let {
                            MuscleGroup(
                                title = "Primary",
                                muscles = it
                            )
                        }


                        // Secondary
                        exercise.secondaryMuscles?.let {
                            MuscleGroup(
                                title = "Secondary",
                                muscles = it
                            )
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Instructions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        exercise.instructions?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun MuscleGroup(
    title: String,
    muscles: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            muscles
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { muscle ->

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(muscle)
                        }
                    )
                }
        }
    }
}




@Composable
fun InfoItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String?
) {
    if (value == null) return

    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally){
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Preview(showBackground = true)
@Composable
fun ExerciseDetailsPreview() {
    val navController = rememberNavController()
    val mockExercise = Exercise(
        id = "0001",
        name = "Bench Press",
        force = "push",
        level = "beginner",
        mechanic = "compound",
        equipment = "barbell",
        instructions = "Lie on the bench and press the bar upward.",
        category = "strength",
        primaryMuscles = """["chest"]""",
        secondaryMuscles = """["triceps", "shoulders"]""",
        imagePath = "images/bench_press.jpg",
        isUserCreated = false
    )
    MaterialTheme {
        ExerciseDetails(mockExercise.id, navController = navController)
    }
}