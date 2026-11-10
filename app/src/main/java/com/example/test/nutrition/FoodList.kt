package com.example.test.nutrition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.test.ExerciseRow
import com.example.test.data.Exercise
import com.example.test.data.NutrtionData.Food
import com.example.test.data.NutrtionData.FoodUiState
import com.example.test.data.NutrtionData.NutritionViewModel
import com.example.test.data.NutrtionData.UsdaFood
import com.example.test.searchTopBar
import java.net.URLEncoder

@Composable
fun FoodList(viewModel: NutritionViewModel = viewModel(),
             date: String,
             category: String = "Other",
             navController: NavController
){
//    var isSearching by remember { mutableStateOf(false) }
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchText by viewModel.searchQuery.collectAsStateWithLifecycle()
//    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()


//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            searchTopBar(
                title = "Food",
                isSearching = isSearching,
                searchText = searchText,
                onSearchTextChanged = viewModel::onSearchTextChanged,

                onSearchClick = viewModel::onSearchClick,

                onSearchCancel = viewModel::onSearchCancel,

                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)){
            if (searchResults.itemCount == 0 && searchText.isBlank()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Type to search foods")
                }
            }
            else{
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults.itemCount) { index ->
                        val food = searchResults[index]
                        food?.let {
                            FoodRow(
                                food = it,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                servingSize = 0f,
                                kcalPss = 0f,
                                date = date,
                                category = it.dataType,
                                navController = navController
                            )
                        }
                    }
                    when (searchResults.loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                                }
                            }
                        }
                        is LoadState.Error -> {
                            val error = searchResults.loadState.append as LoadState.Error
                            item {
                                Text(text = "Error loading more: ${error.error.localizedMessage}")
                            }
                        }
                        else -> {}
                    }
                }
            }

//            // Display the API response
//            when (val state = uiState) {
//                FoodUiState.Idle -> {
//                    Text(
//                        text = "Press the button to test USDA",
//                        modifier = Modifier.padding(8.dp)
//                    )
//                }
//
//                FoodUiState.Loading -> {
//                    Text(
//                        text = "Loading USDA foods...",
//                        modifier = Modifier.padding(8.dp)
//                    )
//                }
//
//                is FoodUiState.Success -> {
//                    Column(
//                        modifier = Modifier.padding(8.dp)
//                    ) {
//                        Text("USDA results:")
//
//                        state.foods.forEach { food ->
//                            println(food)
//                            Text(
//                                text = ")",
//                                modifier = Modifier.padding(vertical = 4.dp)
//                            )
//                        }
//
//                        if (state.foods.isEmpty()) {
//                            Text("No foods found.")
//                        }
//                    }
//                }
//
//                is FoodUiState.Error -> {
//                    Text(
//                        text = "USDA error: ${state.message}",
//                        modifier = Modifier.padding(8.dp),
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//            }




//            LazyColumn(Modifier.fillMaxSize().padding(8.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//                ){
//                items(count = searchResults.size)
//                {
//                    index ->
//                    val food = searchResults[index]
//                    var ss by remember { mutableFloatStateOf(0F) }
//
//                    LaunchedEffect(food.id) {
//                        ss = viewModel.getFoodServingSize(food.id)
//                    }
//                    food.let {
//                        FoodRow(
//                            food = it,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp),
//                            servingSize = ss,
//                            kcalPss = (food.kcal_100g/100*ss),
//                            date,
//                            category,
//                            navController
//                        )
//                    }
//                }
//            }
        }

    }
}


@Composable
fun FoodRow(
    food: UsdaFood,
    modifier: Modifier = Modifier,
    servingSize: Float,
    kcalPss: Float,
    date: String,
    category: String,
    navController: NavController
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .clickable{
//                navController.navigate("FoodAdd/${date}/${food.id}/${category}")
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
//            Row(modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically) {
//                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = food.description,
                        style = MaterialTheme.typography.titleMedium
                    )
//                }
                    Text(
                        text = "${servingSize}g  ${kcalPss}kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

            }
        }
    }


