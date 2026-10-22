package com.example.test


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.test.data.ExerciseViewModel
import com.example.test.data.*
import java.net.URLEncoder
import java.util.Calendar

@Composable
fun ExerciseList(
    viewModel: ExerciseViewModel = viewModel(),
    date: String,
    navController: NavController
) {
    // 1. Collect the PagingData flow into a LazyPagingItems object.
    // This automatically triggers the DB query for page 1 (first 20).
    val exercises = viewModel.exercisesPaged.collectAsLazyPagingItems()
    var isSearching by remember { mutableStateOf(false) }
    val searchText by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()


    Log.d("test", "exercisellist")
    Scaffold(
        topBar = {
            searchTopBar(
                title = "Exercises",
                isSearching = isSearching,
                searchText = searchText,
                onSearchTextChanged = { query ->
                    viewModel.searchExerciseByName(query)
                },

                onSearchClick = {
                    isSearching = true
                },

                onSearchCancel = {
                    isSearching = false
                    viewModel.searchExerciseByName("")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { paddingValues ->
        if(!isSearching) {
            Box(modifier = Modifier.padding(paddingValues)) {
                // 2. Check the REFRESH state (initial load or pull-to-refresh)
//                when (val refreshState = exercises.loadState.refresh) {
//                    is LoadState.Loading -> {
//                        // Full screen loader for the very first page
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CircularProgressIndicator()
//                        }
//                    }
//
//                    is LoadState.Error -> {
//                        // Full screen error if the FIRST page fails (e.g., DB corruption, but usually rare)
//                        Column(
//                            modifier = Modifier.fillMaxSize(),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center
//                        ) {
//                            Text(text = "Failed to load exercises: ${refreshState.error.message}")
//                            Spacer(modifier = Modifier.height(16.dp))
//                            Button(onClick = { exercises.retry() }) {
//                                Text("Retry")
//                            }
//                        }
//                    }
//
//                    else -> {
//                        // 3. The main list (Success state)
                        LazyColumn(
                            Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Display the items (20 at a time)
                            items(
                                count = exercises.itemCount,
                                key = { index -> exercises[index]?.id ?: index }
                            ) { index ->
                                val exercise = exercises[index]
                                exercise?.let {
                                    ExerciseRow(
                                        exercise = it,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        date,
                                        navController
                                    )
                                }
                            }
                        }
//
//                            when (val appendState = exercises.loadState.append) {
//                                is LoadState.Loading -> {
//                                    // Footer loading spinner when scrolling down
//                                    item {
//                                        Box(
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .padding(16.dp),
//                                            contentAlignment = Alignment.Center
//                                        ) {
//                                            CircularProgressIndicator()
//                                        }
//                                    }
//                                }
//
//                                is LoadState.Error -> {
//                                    // Footer error with retry button for the next page
//                                    item {
//                                        Column(
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .padding(16.dp),
//                                            horizontalAlignment = Alignment.CenterHorizontally
//                                        ) {
//                                            Text(
//                                                text = "Failed to load more: ${appendState.error.message}",
//                                                color = MaterialTheme.colorScheme.error
//                                            )
//                                            Spacer(modifier = Modifier.height(8.dp))
//                                            Button(onClick = { exercises.retry() }) {
//                                                Text("Retry")
//                                            }
//                                        }
//                                    }
//                                }
//
//                                else -> {}
//                            }
//                        }
//                    }
//                }
            }
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
//                items(
//                    count = exercises.itemCount,
//                    key = { index -> exercises[index]?.id ?: index }
//                ) { index ->
//                    val exercise = exercises[index]
//                    exercise?.let {
//                        ExerciseRow(
//                            exercise = it,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp),
//                            date,
//                            navController
//                        )
//                    }
//                }


                items(searchResults) { exercise ->

                        ExerciseRow(
                            exercise = exercise,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            date,
                            navController
                        )
                }
            }
        }
    }
}

@Composable
fun ExerciseRow(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    date: String,
    navController: NavController
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (exercise.isUserCreated) {
                        Text(
                            text = "⭐ User Created",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(
                    onClick = {
                        val encodedName = URLEncoder.encode(exercise.name, "UTF-8")
                        navController.navigate("ExerciseAdd/${date}/${encodedName}")
                    }, modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Exercise"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        navController.navigate("ExerciseDetails/${exercise.id}")
                    }, modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Exercise Information"
                    )
                }
//            exercise.instructions?.let {
//                Text(
//                    text = it,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun ExerciseListPreview() {
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
    val navController = rememberNavController()
    MaterialTheme {
        ExerciseRow(mockExercise, Modifier, "2026-09-30", navController)
    }
}