package com.example.test.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.test.data.NutrtionData.NutritionViewModel
import com.example.test.topBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodAdd(viewModel: NutritionViewModel = viewModel(),
            navController: NavController
) {

    var amount by remember {mutableStateOf("0")}

    var ssExpanded by remember {mutableStateOf(false)}
    var ssSelectedOption by remember { mutableStateOf("g")}
    val ssOptions = listOf("g", "oz")

    var catExpanded by remember {mutableStateOf(false)}
    var catSelectedOption by remember { mutableStateOf("Snack/Other")}
    val catOptions = listOf("Snack/Other", "Breakfast", "Lunch", "Dinner")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        topBar(
            title = "",
            onBackClick = {
                navController.popBackStack()
            }
        )
        Column(
            modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Amount", style = MaterialTheme.typography.bodyLarge)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        // Only allow numbers and decimal points
                        if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = newValue
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(240.dp),
                    singleLine = true,
                    placeholder = { Text("0") }
                )
            }

            // serving size dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Serving Size", style = MaterialTheme.typography.bodyLarge)
                ExposedDropdownMenuBox(
                    expanded = ssExpanded,
                    onExpandedChange = {ssExpanded = !ssExpanded},
                    modifier = Modifier.width(240.dp)
                ) {
                    TextField(
                        value = ssSelectedOption,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = ssExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = ssExpanded,
                        onDismissRequest = {ssExpanded = false}
                    ) {
                        ssOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {Text(option, color = MaterialTheme.colorScheme.onSurface)},
                                onClick = {ssSelectedOption = option
                                ssExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }

            //Category Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Category", style = MaterialTheme.typography.bodyLarge)
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = {catExpanded = !catExpanded},
                    modifier = Modifier.width(240.dp)
                ) {
                    TextField(
                        value = catSelectedOption,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = {catExpanded = false}
                    ) {
                        catOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {Text(option, color = MaterialTheme.colorScheme.onSurface)},
                                onClick = {catSelectedOption = option
                                    catExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Information for ${amount}${ssSelectedOption}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "kcal",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "carb",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "protein",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "fat",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}

@Preview(showBackground = true)
@Composable
fun FoodAddPreview() {
    val navController = rememberNavController()
    MaterialTheme{
        FoodAdd(navController = navController)
    }
}