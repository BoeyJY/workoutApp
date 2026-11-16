package com.example.test


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.test.data.ExerciseViewModel
import com.example.test.data.MaxWeightPerDay
import com.example.test.data.download.DownloadViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.List
import kotlin.collections.emptyList
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun calculateStep(min: Float, max: Float, targetIntervals: Int = 5): Float {
    val range = (max - min).toDouble()
    if (range <= 0.0) return 1F

    val rawStep = range / targetIntervals
    val magnitude = 10.0.pow(floor(log10(rawStep)))
    val normalized = rawStep / magnitude

    val niceNormalized = when {
        normalized <= 1.0 -> 1.0
        normalized <= 2.0 -> 2.0
        normalized <= 2.5 -> 2.5
        normalized <= 5.0 -> 5.0
        else -> 10.0
    }
    return (niceNormalized * magnitude).toFloat()
}

@Composable
private fun ComposeBasicLineChart(
    modelProducer: CartesianChartModelProducer,
    step: Float,
    rangeProvider: CartesianLayerRangeProvider,
    points: List<Pair<LocalDate, Float>>,
    modifier: Modifier = Modifier,
) {

    val labelComponent = rememberAxisLabelComponent(
        style = TextStyle(color = Color.Black)
    )

    val tapMarker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(style = TextStyle(color = Color.Black)),
        indicator = remember {
            { color ->
                ShapeComponent(
                    fill = Fill(color),
                    shape = CircleShape
                )
            } },
//        guideline = rememberLineComponent(fill = Fill(Color.Gray)),
    )

    val persistentMarker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(),
        valueFormatter = { _, _ -> "" },
        indicator = remember {
            { color ->
                ShapeComponent(
                    fill = Fill(color),
                    shape = CircleShape
                )
        } },
//        guideline = rememberLineComponent(fill = Fill(Color.Gray)),
    )

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    rangeProvider = rangeProvider
                ),
                startAxis = VerticalAxis.rememberStart(
                    title = { "Weight (kg)" },
//                    titleComponent = labelComponent,
                    label = labelComponent,
                    itemPlacer = remember {
                    VerticalAxis.ItemPlacer.step(
                        step = { step.toDouble() }
                    )
                }),
                bottomAxis = HorizontalAxis.rememberBottom(
                    title = {"Date"},
//                    titleComponent = labelComponent,
                    label = labelComponent,
                    itemPlacer = remember {
                        HorizontalAxis.ItemPlacer.extremes()
                    },
                    valueFormatter = remember {
                        CartesianValueFormatter { _, value, _ ->
                            LocalDate.ofEpochDay(value.toLong())
                                .format(DateTimeFormatter.ofPattern("d MMM yyyy"))
                        }
                    }
                ),
                marker = tapMarker,
                persistentMarkers = {
                    points.forEach { (date,_) ->
                        persistentMarker at date.toEpochDay().toFloat()
                    }
                }
            ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ExerciseGraph(
    modifier: Modifier = Modifier,
    viewModel: ExerciseViewModel = viewModel(),
    navController: NavController,
    exerciseId: String
) {

    val isoDate = DateTimeFormatter.ISO_LOCAL_DATE

//    var dailyMax by remember { mutableStateOf(emptyList<MaxWeightPerDay>()) }
    val uiState by viewModel.graphUiState.collectAsStateWithLifecycle()




    LaunchedEffect(exerciseId) {
        viewModel.getMaxWeightPerDay(exerciseId)
    }
//    val dailyMax by viewModel.getMaxWeightPerDay(exerciseId)
//        .collectAsStateWithLifecycle(initialValue = emptyList())


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

            val step = remember(points) {
                val minWeight = points.minOfOrNull { it.second } ?: return@remember 1F
                val maxWeight = points.maxOfOrNull { it.second } ?: return@remember 1F
                calculateStep(minWeight, maxWeight)
            }

            val rangeProvider = remember(step) {
                val minWeight = points.minOfOrNull { it.second } ?: 0F
                val maxWeight = points.maxOfOrNull { it.second } ?: 0F
                CartesianLayerRangeProvider.fixed(
                    minY = minWeight - step.toDouble(),
                    maxY = maxWeight + step.toDouble()
                )
            }

            Scaffold(
                topBar = {
                    topBar(
                        "Workouts",
                        onBackClick = dropUnlessResumed{
                    navController.popBackStack()
                        })
                }
            ) {
                innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ComposeBasicLineChart(modelProducer, step, rangeProvider, points, modifier)
                }
            }
        }
    }
}



