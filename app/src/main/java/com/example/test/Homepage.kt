package com.example.test

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@Composable
fun HomePage(viewModel: DownloadViewModel = viewModel()) {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }




    Log.d("test", "homepage")
    Scaffold(
        topBar = {
            topBar("Home")
        }
    )
    { innerPadding ->


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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF211D2C)
                )
            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = "Get started",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This is your main homepage area. " +
                                "You can put your app's main feature here.",
                        color = Color.White,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            // Put something here later
                            viewModel.dbReady()
                        }
                    ) {
                        Text("Get Started")
                    }
                }
            }

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



@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    MaterialTheme {
        HomePage()
    }
}