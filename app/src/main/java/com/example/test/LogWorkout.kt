package com.example.test

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CommentBank
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room3.Embedded
import androidx.room3.Relation
import com.example.test.data.ExerciseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.test.data.sessionExercises
import com.example.test.data.sessionSets
import java.net.URLEncoder
import java.util.Date

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun LogWorkout(viewModel: ExerciseViewModel = viewModel(),
               navController: NavController,
               navDate: String? = null) {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }
//    var selectedDate by remember(navDate){ mutableStateOf(initialDateHelper(navDate)) }

    val selectedDate = viewModel.logWorkoutDate
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]

//    val date = SimpleDateFormat(
//        "yyyy-MM-dd"
//    ).format(selectedDate.time)

    var lastAppliedNavDate by rememberSaveable { mutableStateOf<String?>(null) }
    val sets = viewModel.sets
    LaunchedEffect(navDate) {
        if (navDate != null && navDate != selectedDate && navDate != lastAppliedNavDate) {
            println("lgoworkout navDate not working")
            viewModel.updateLogWorkoutDate(navDate)
            lastAppliedNavDate = navDate
        }
    }

    LaunchedEffect(selectedDate) {
        viewModel.getSessionExercises(selectedDate)
    }
    val cards = sets.groupBy { it.name }

    val date = remember(selectedDate) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        Calendar.getInstance().apply {
            time = sdf.parse(selectedDate) ?: Date()
        }
    }

    Scaffold(
        topBar = {
            topBar("Workouts"){
                TopBarIcon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Calendar",
                    onClick = dropUnlessResumed{ navController.navigate("CalendarScreen") }
                )
            }
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
                            viewModel.updateLogWorkoutDate(newDateStr)
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
                            viewModel.updateLogWorkoutDate(newDateStr)
                        }, modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next day"
                        )
                    }
                }
            }
            AnimatedContent(
                targetState = selectedDate,
                transitionSpec ={
                    val forward = targetState > initialState

                    if (forward){
                        slideInHorizontally { it } togetherWith
                                slideOutHorizontally { -it }
                    } else{
                        slideInHorizontally { -it } togetherWith
                                slideOutHorizontally { it }
                    }
                }
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {

                    if (sets.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(cards.toList()) { (name, set) ->
                                val encodedName = URLEncoder.encode(name, "UTF-8")
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable(onClick = dropUnlessResumed {
                                            val date = SimpleDateFormat(
                                                "yyyy-MM-dd"
                                            ).format(date.time)
                                            navController.navigate("ExerciseAdd/${date}/${encodedName}")
                                        }),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {

                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {

                                        // Exercise name
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Individual sets
                                        set.forEach { set ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CommentBank,
                                                    contentDescription = if (set.comment.isNullOrEmpty()) null else "Has comment",
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .padding(end = 6.dp),
                                                    tint = if (set.comment.isNullOrEmpty()) {
                                                        Color.Transparent
                                                    } else {
                                                        MaterialTheme.colorScheme.primary
                                                    }

                                                )

                                                // Weight
                                                Text(
                                                    text = "${set.weight} kg",
                                                    modifier = Modifier.weight(1f)
                                                )

                                                // Reps
                                                Text(
                                                    text = "${set.reps} reps",
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        modifier =
                            if (sets.isEmpty()) {
                                Modifier.align(Alignment.Center)
                            } else {
                                Modifier.align(Alignment.BottomEnd)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = dropUnlessResumed {
                                val date = SimpleDateFormat(
                                    "yyyy-MM-dd"
                                ).format(date.time)
                                navController.navigate("ExerciseList/$date")
                            },
                            modifier = if (sets.isEmpty()) {
                                Modifier
//                                .align(Alignment.Center)
                                    .size(80.dp)
                            } else {
                                Modifier
//                                .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                                    .size(64.dp)
                            },
                            shape = CircleShape
                        ) {
                            Text(
                                text = "+",
                                fontSize = if (sets.isEmpty()) 32.sp else 28.sp
                            )
                        }
                        if (sets.isEmpty()) {
                            OutlinedButton(
                                onClick = dropUnlessResumed {
                                    val date = SimpleDateFormat(
                                        "yyyy-MM-dd"
                                    ).format(date.time)
                                    navController.navigate("CalendarScreen?navDate=$date&copy=${true}")
                                },
                                modifier =
                                    Modifier
                                        .size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CopyAll,
                                    contentDescription = "Copy previous workout",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            }
        }
    }





@Preview(showBackground = true)
@Composable
fun LogWorkoutPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        LogWorkout(navController = navController)
    }
}