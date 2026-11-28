package com.example.test

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.test.data.NutrtionData.Food
import com.example.test.data.download.DownloadViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@Composable
fun HomePage(viewModel: DownloadViewModel = viewModel()) {


    Scaffold(
        topBar = {
            topBar("Home")
        }
    )
    { innerPadding ->


        val macros by viewModel.dailyMacros.collectAsStateWithLifecycle()

        val pagerItems = listOf(
            MacroCardData("Calories", macros.cal,     2_000f, Color(0xFF7EC8D3)),
            MacroCardData("Carbs",    macros.carb,    250f,   Color(0xFFFF9800)),
            MacroCardData("Protein",  macros.protein, 160f,   Color(0xFF4CAF50)),
            MacroCardData("Fat",      macros.fat,     80f,    Color(0xFF2196F3))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {

            // Header


                Text(
                    text = "Hello! 👋",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome to MyFirstApp",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main card

            SwipeableMacroCards(pagerItems)
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(20.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = Color(0xFF211D2C)
//                )
//            ) {
//
//                Column(
//                    modifier = Modifier.padding(24.dp)
//                ) {
//
//                    Text(
//                        text = "Get started",
//                        color = Color.White,
//                        fontSize = 22.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Text(
//                        text = "This is your main homepage area. " +
//                                "You can put your app's main feature here.",
//                        color = Color.White,
//                        fontSize = 15.sp
//                    )
//
//                    Spacer(modifier = Modifier.height(20.dp))
//
//                    Button(
//                        onClick = {
//                            // Put something here later
//                            viewModel.dbReady()
//                        }
//                    ) {
//                        Text("Get Started")
//                    }
//                }
//            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quick Actions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick action cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                QuickActionCard(
                    title = "Action 1",
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    title = "Action 2",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                QuickActionCard(
                    title = "Action 3",
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    title = "Action 4",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }



    val ready = viewModel.dbReady
    LaunchedEffect(viewModel) {
        if (ready == null) {
            viewModel.dbReady()
        }
    }

    val downloadState by viewModel.downloadState
        .collectAsStateWithLifecycle()

    var showDialog = viewModel.showDialog

    if (showDialog
//        && downloadState != DownloadViewModel.DownloadState.Success
        && ready == false) {
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissDialog()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ArrowCircleDown,
                    contentDescription = "Download Resources",
                    tint = MaterialTheme.colorScheme.surfaceVariant
                )
            },
            title = {
                Text("Resources needs to be downloaded?")
            },
            text = {
                when (val state = downloadState) {
                    DownloadViewModel.DownloadState.NotStarted -> {
                        Text(
                            "Click continue to begin the download"
                        )
                    }
                    DownloadViewModel.DownloadState.Downloading -> {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Downloading database...")
                    }
                    DownloadViewModel.DownloadState.Success -> {

                        Text("Download complete!")
                    }
                    is DownloadViewModel.DownloadState.Error -> {
                        Text(
                            "Download failed: ${state.message}"
                        )
                    }
                }
            },
            confirmButton = {
                when (downloadState) {
                    DownloadViewModel.DownloadState.NotStarted,
                    is DownloadViewModel.DownloadState.Error -> {
                        TextButton(
                            onClick = {
                                viewModel.refreshExercisesFromNetwork()
                            }
                        ) {
                            Text(
                                if (downloadState is DownloadViewModel.DownloadState.Error)
                                    "Retry"
                                else
                                    "Download", color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    DownloadViewModel.DownloadState.Downloading -> {
                        // No button while downloading.
                    }

                    DownloadViewModel.DownloadState.Success -> {
                        Button(
                            onClick = {
                                viewModel.dismissDialog()
                            }
                        ) {
                            Text("Done")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissDialog() }
                ) {
                    Text("Cancel", color = AlertDialogDefaults.textContentColor)
                }
            }
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}


data class MacroCardData(
    val label: String,
    val current: Float,
    val goal: Float,
    val color: Color
)

@Composable
fun SwipeableMacroCards(items: List<MacroCardData>) {


    val pagerState = rememberPagerState { items.size }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pager + Manual Buttons
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp), // Space for buttons
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) { page ->
                val macro = items[page]
                SemiCircleMacroCard(macro)
            }

            // Left Button
            if (pagerState.currentPage > 0) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
                }
            }

            // Right Button
            if (pagerState.currentPage < items.size - 1) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next")
                }
            }
        }

        // Dot Indicator
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(items.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(if (pagerState.currentPage == iteration) 10.dp else 8.dp)
                        .background(color, shape = androidx.compose.foundation.shape.CircleShape)
                )
            }
        }
    }
}


@Composable
fun SemiCircleMacroCard(data: MacroCardData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Custom Semi-Circle Gauge
            val progress = (data.current / data.goal).coerceIn(0f, 1f)
            val backgroundColor = data.color.copy(alpha = 0.2f)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Canvas(modifier = Modifier.size(200.dp, 100.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    // Background Arc
                    drawArc(
                        color = backgroundColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height * 2 - strokeWidth),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Progress Arc
                    drawArc(
                        color = data.color,
                        startAngle = 180f,
                        sweepAngle = 180f * progress,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height * 2 - strokeWidth),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                // Text overlay
                Text(
                    text = "${data.current.toInt()} / ${data.goal.toInt()}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 40.dp) // Adjust for arc
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    MaterialTheme {
        HomePage()
    }
}