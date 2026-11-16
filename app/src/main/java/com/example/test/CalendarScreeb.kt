package com.example.test

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CommentBank
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.example.test.data.ExerciseViewModel

@Composable
fun CalendarScreen(viewModel: ExerciseViewModel = viewModel(),
                   navController: NavController,
                   navDate: String? = null,
                   copy: Boolean = false){

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    var popUpDate by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            topBar(if (!copy){"Calendar"} else{"Choose a date to copy"},
                onBackClick = dropUnlessResumed{
                navController.popBackStack()})
        }
    ) {
        innerPadding ->
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
                        onClick =  { currentMonth = currentMonth.minusMonths(1) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Month"
                        )
                    }

                    Text(
                        text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {currentMonth = currentMonth.plusMonths(1) }
                        , modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Month"
                        )
                    }
                }
            }
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(1.dp, Color.Black)
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .wrapContentHeight(Alignment.CenterVertically),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            CalendarGrid(currentMonth = currentMonth,
                onDateClick = { dateString ->
                    // 1. Pass to ViewModel to run the query
                    viewModel.calendarClicked(dateString)
                    popUpDate = dateString
                })
            if (popUpDate != null) {
                DatePopupDialog(
                    date = popUpDate!!,
                    viewModel = viewModel,
                    onDismiss = { popUpDate = null },
                    navDate = navDate,
                    copy = copy,
//                    onNavigateToDetails = {
//                        // Dismiss popup and navigate
//                        popUpDate = null
////                        onNavigateToDetails(it)
//                    },
                    navController
                )
            }
        }



    }

}

@Composable
fun DatePopupDialog(
    date: String,
    viewModel: ExerciseViewModel,
    onDismiss: () -> Unit,
    navDate: String?,
    copy: Boolean,
//    onNavigateToDetails: (String) -> Unit,
    navController: NavController
) {
    // Observe the 'sets' state from your ViewModel
    val sets = viewModel.sets
    val cards = sets.groupBy { it.name }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp), // Fixed height so the LazyColumn knows how much space it has
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // --- HEADER ---
                Text(
                    text = "Exercises for $date",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // --- LAZY COLUMN BODY ---
                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // Takes up all remaining space above the buttons
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (sets.isEmpty()) {
                        item {
                            Text(
                                text = "No exercises logged for this day.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        // Assuming 'dailyExerciseCard' has fields like exerciseName, weight, reps
                        items(cards.toList()) {(name, set) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
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
                                    }                                    // Add comment display if available in your data class
                                }
                            }
                        }
                    }
                }

                // --- BUTTONS BENEATH THE LIST ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if(copy && !navDate.isNullOrEmpty()) {
                            if(viewModel.isCopying){
                                CircularProgressIndicator()
                            } else {
                                TextButton(onClick = onDismiss) {
                                    Text("Close")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(onClick = dropUnlessResumed{

                                    viewModel.copyPreviousWorkout(sets, navDate) {

                                        navController.navigate("LogWorkout?navDate=$navDate"){
                                            popUpTo("HomePage") { inclusive = false }
                                        }
                                        onDismiss()

                                    }
                                }) {
                                    Text("Copy to today")
                                }
                            }
                        } else {
                            TextButton(onClick = onDismiss) {
                                Text("Close")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(onClick = dropUnlessResumed{

                                navController.navigate("LogWorkout?navDate=$date")
                                onDismiss()
                            }) {
                                Text("Full Details")
                            }
                        }


                    }
                }

        }
    }
}

@Composable
fun CalendarGrid(currentMonth: YearMonth,
                 onDateClick: (String) -> Unit) {
    // Calculate the offset (how many empty days before the 1st of the month)
    // DayOfWeek.value returns 1 for Monday, 7 for Sunday.
    // Modulo 7 converts this to 1 for Monday and 0 for Sunday, perfect for a Sunday-start calendar.
    val firstDayOfMonth = currentMonth.atDay(1)
    val offset = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()

    // We always render 6 rows (42 cells) maximum to ensure the UI height NEVER changes when switching months.
    val totalRows = 6

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight() // Takes up all remaining vertical space on the screen
    ) {
        for (row in 0 until totalRows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Each row equally divides the vertical space
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - offset + 1

                    // Check if this cell contains a valid day of the current month
                    val isValidDay = dayNumber in 1..daysInMonth

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(0.5.dp, Color.LightGray)
                            .clickable(enabled = isValidDay){
                                if (isValidDay) {
                                    val clickedDate = currentMonth.atDay(dayNumber).toString()
                                    onDateClick(clickedDate)
                                }
                            }, // Light inner grid borders
                        contentAlignment = Alignment.Center
                    ) {
                        if (isValidDay) {
                            Text(
                                text = dayNumber.toString(),
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        CalendarScreen(navController = navController)
    }
}