package com.example.test.nutrition

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.test.data.NutrtionData.FoodSource
import com.example.test.data.NutrtionData.NutritionViewModel
import com.example.test.data.NutrtionData.UsdaFood
import com.example.test.searchTopBar

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

    val currentSource by viewModel.foodSource.collectAsStateWithLifecycle()

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
        Column(Modifier.fillMaxSize().padding(paddingValues)){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            )
            {
                Button(
                    onClick = { viewModel.setFoodSource(FoodSource.CACHE) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentSource == FoodSource.CACHE)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("History")
                }
                Button(
                    onClick = { viewModel.setFoodSource(FoodSource.API) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentSource == FoodSource.API)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("API")
                }
            }

//        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            if (searchResults.itemCount == 0 && searchText.isBlank()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Type to search foods")
                }
            } else {
                val isCache = currentSource == FoodSource.CACHE
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults.itemCount) { index ->
                        val food = searchResults[index]
                        val cals: Float = food?.foodNutrients
                            ?.firstOrNull {
                                it.nutrientId in setOf(1008, 2047, 2048) &&
                                        it.unitName.equals("kcal", ignoreCase = true)
                            }
                            ?.value
//                            ?.firstOrNull { it.name == "Energy" }
//                            ?.amount
                            ?: 0F
                        food?.let {
                            FoodRow(
                                food = it,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                servingSize = it.servingSize,
                                kcalP100 = cals,
                                date = date,
                                category = category,
                                brand = it.brandName,
                                isCache = isCache,
                                onDelete = { viewModel.deleteFromCache(food.fdcId) },
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
//        }

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
    servingSize: Float?,
    kcalP100: Float,
    date: String,
    category: String,
    brand: String?,
    isCache: Boolean,
    onDelete: () -> Unit,
    navController: NavController
) {

    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember {mutableStateOf(false)}
    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = modifier.fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        navController.navigate(
                            "FoodAdd/${date}/${food.fdcId}/${category}?servingSize=${food.servingSize}&brand=${food.brandName}"
                        )
                    },
                    onLongClick = if (isCache) {
                        { expanded = true }
                    } else null
                )
//            .clickable{
//                navController.navigate(
//                    "FoodAdd/${date}/${food.fdcId}/${category}?servingSize=${food.servingSize}&brand=${food.brandName}")
//            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
//            Row(modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically) {
//                Column(modifier = Modifier.weight(1f)) {
                val grams = servingSize ?: 100F
                Text(
                    text = food.description,
                    style = MaterialTheme.typography.titleMedium
                )
//             }
                Text(
                    text = "${grams}g  ${kcalP100 / 100 * grams}kcal ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = if (brand.isNullOrEmpty()) {
                        "source: ${food.dataType}"
                    } else {
                        "source: ${food.dataType} brand: $brand"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Delete from your history") },
                onClick = {
                    expanded = false
                    showDeleteDialog = true
//                    onDelete()
                },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
            )
        }

        if (showDeleteDialog){
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                title = { Text("Remove from your history?") },
                text = { Text("Are you sure you want to remove '${food.description}' from your saved foods?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onDelete() // Trigger the ViewModel delete function
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


