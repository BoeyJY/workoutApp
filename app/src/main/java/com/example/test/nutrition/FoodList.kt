package com.example.test.nutrition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.test.ExerciseRow
import com.example.test.data.Exercise
import com.example.test.data.NutrtionData.Food
import com.example.test.data.NutrtionData.NutritionViewModel
import com.example.test.searchTopBar
import java.net.URLEncoder

@Composable
fun FoodList(viewModel: NutritionViewModel = viewModel(),
             navController: NavController
){
    var isSearching by remember { mutableStateOf(false) }
    val searchText by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            searchTopBar(
                title = "Food",
                isSearching = isSearching,
                searchText = searchText,
                onSearchTextChanged = { query ->
                    viewModel.searchFoodByName(query)
                },

                onSearchClick = {
                    isSearching = true
                },

                onSearchCancel = {
                    isSearching = false
                    viewModel.searchFoodByName("")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)){
            LazyColumn(Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                items(count = searchResults.size)
                {
                    index ->
                    val food = searchResults[index]
                    var ss by remember { mutableFloatStateOf(0F) }

                    LaunchedEffect(food.id) {
                        ss = viewModel.getFoodServingSize(food.id)
                    }
                    println(food.id)
                    println(ss)
                    food.let {
                        FoodRow(
                            food = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            servingSize = ss,
                            kcalPss = (food.kcal_100g/100*ss),

//                            date,
                            navController
                        )
                    }
                }
            }
        }

    }
}


@Composable
fun FoodRow(
    food: Food,
    modifier: Modifier = Modifier,
    servingSize: Float,
    kcalPss: Float,
//    date: String,
    navController: NavController
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .clickable{
                navController.navigate("FoodAdd")
                //TODO direct to add food page
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
//            Row(modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically) {
//                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = food.name,
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


