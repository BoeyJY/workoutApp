package com.example.test


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.data.ExerciseViewModel
import com.example.test.data.MaxWeightPerDay
import com.example.test.data.download.DownloadViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.List
import kotlin.collections.emptyList

@Composable
private fun ComposeBasicLineChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(
                    title = { "Weight (kg)" },
                    itemPlacer = remember {
                    VerticalAxis.ItemPlacer.step(
                        step = { 10.0 }
                    )
                }),
                bottomAxis = HorizontalAxis.rememberBottom(
                    title = {"Date"},
                    itemPlacer = remember {
                        HorizontalAxis.ItemPlacer.aligned()
                    }
                ),
            ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
    )
}

@Composable
fun ExerciseGraph(
    modifier: Modifier = Modifier,
    viewModel: ExerciseViewModel = viewModel(),
    exerciseId: String
) {
    val isoDate = DateTimeFormatter.ISO_LOCAL_DATE

    var dailyMax by remember { mutableStateOf(emptyList<MaxWeightPerDay>()) }
    val uiState by viewModel.graphUiState.collectAsStateWithLifecycle()



    LaunchedEffect(exerciseId) {
        viewModel.getMaxWeightPerDay(exerciseId)
    }
//    val dailyMax by viewModel.getMaxWeightPerDay(exerciseId)
//        .collectAsStateWithLifecycle(initialValue = emptyList())

    println("id $exerciseId")
    println(dailyMax)

    when {
        uiState.isLoading -> {
            CircularProgressIndicator()
        }

        uiState.error != null -> {
            Text("Error: ${uiState.error}")
        }

        uiState.data.isEmpty() -> {
            Text("No workout data available")
        }

        else -> {
            val dailyMax = uiState.data

            val points = remember(dailyMax) {
                dailyMax
                    .mapNotNull { row ->
                        runCatching {
                            LocalDate.parse(row.date, isoDate) to row.weight
                        }.getOrNull()
                    }.sortedBy { it.first }
            }

            val modelProducer = remember { CartesianChartModelProducer() }

            LaunchedEffect(points) {
                modelProducer.runTransaction {
                    // Learn more: https://patrykandpatrick.com/z5ah6v.
                    lineModel {
                        series(
                            x = points.map { it.first.toEpochDay().toFloat() },
                            y = points.map { it.second }
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                ComposeBasicLineChart(modelProducer, modifier)
            }
        }
    }
}



@Composable
internal fun PreviewBox(content: @Composable BoxScope.() -> Unit) {
    Box(modifier = Modifier.background(Color.White).padding(16.dp), content = content)
}

@Composable
@Preview
fun ComposeBasicLineChartPreview() {
    val modelProducer = remember { CartesianChartModelProducer() }
    // Use `runBlocking` only for previews, which don’t support asynchronous execution.
    runBlocking {
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/z5ah6v.
            lineModel { series(13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11) }
        }
    }
    PreviewBox { ComposeBasicLineChart(modelProducer) }
}