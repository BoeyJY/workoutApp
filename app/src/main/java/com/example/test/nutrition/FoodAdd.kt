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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.test.data.NutrtionData.Food
import com.example.test.data.NutrtionData.NutritionViewModel
import com.example.test.topBar
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodAdd(viewModel: NutritionViewModel = viewModel(),
            date: String,
            foodId: String,
            category: String,
            loggedId: String? = null,
            navController: NavController
) {

    var amount by remember {mutableStateOf("")}


    var ssExpanded by remember {mutableStateOf(false)}
    var ssSelectedOption by remember { mutableStateOf("g")}
    val ssOptions = listOf("g", "oz")

    var catExpanded by remember {mutableStateOf(false)}
    var catSelectedOption by remember { mutableStateOf(category)}
    val catOptions = listOf("Other", "Breakfast", "Lunch", "Dinner")

    val id = foodId.toLong()
    var selectedFood by remember{mutableStateOf<Food?>(null)}
    LaunchedEffect(id)
    {
        selectedFood = viewModel.getFoodById(id)
        if (loggedId != null) {

            val updateFood = viewModel.getLoggedFoodsById(loggedId.toLong())
            amount = updateFood.weight.toString()
            ssSelectedOption = updateFood.servingSize
            catSelectedOption = updateFood.category
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        topBar(
            title = selectedFood?.name ?: "error",
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
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
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
                            text = "kcal: ${String.format(LocalLocale.current.platformLocale, "%.2f",
                                (selectedFood?.kcal_100g?: 0F) / 100 * (amount.toFloatOrNull()?: 0F))}",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "carb: ${String.format(LocalLocale.current.platformLocale, "%.2f",
                                (selectedFood?.carbs_100g?: 0F) / 100 * (amount.toFloatOrNull()?: 0F))}",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "protein: ${String.format(LocalLocale.current.platformLocale, "%.2f",
                                (selectedFood?.protein_100g?: 0F) / 100 * (amount.toFloatOrNull()?: 0F))}",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "fat: ${String.format(LocalLocale.current.platformLocale, "%.2f",
                                (selectedFood?.fat_100g?: 0F) / 100 * (amount.toFloatOrNull()?: 0F))}",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start) {
                Text(
                    text = "fiber: ${String.format(LocalLocale.current.platformLocale, "%.2f",
                        (selectedFood?.fiber_100g?: 0F) / 100 * (amount.toFloatOrNull()?: 0F))}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "sugar: ${String.format(LocalLocale.current.platformLocale, "%.2f",
                        (selectedFood?.sugar_100g?: 0F) / 100 * (amount.toFloatOrNull()?: 0F))}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "saturated fat: ${String.format(LocalLocale.current.platformLocale, "%.2f",
                        (selectedFood?.sat_fat_100g?: 0F) / 100 * (amount.toFloatOrNull()?: 0F))}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "sodium: ${String.format(LocalLocale.current.platformLocale, "%.2f",
                        (selectedFood?.sodium_100g?: 0F) / 100 * (amount.toFloatOrNull()?: 0F))}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Brand: ${selectedFood?.brand?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Country: ${selectedFood?.country?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Barcode: ${selectedFood?.barcode?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                if(loggedId == null) {
                    Button(
                        onClick = {
                            viewModel.insertLoggedFood(
                                foodId = id,
                                name = selectedFood?.name ?: "error",
                                Date = date,
                                category = catSelectedOption,
                                weight = amount.toFloat(),
                                servingSize = ssSelectedOption,
                                kcal = (selectedFood?.kcal_100g
                                    ?: 0F) / 100 * (amount.toFloatOrNull() ?: 0F)
                            )
                            navController.navigate("FoodLog?navDate=${date}")
                        },
                        enabled = (amount.toFloatOrNull() ?: 0f) > 0
                    ) {
                        Text("Add")
                    }
                }
                else{

                    var showDialog by remember { mutableStateOf(false)}
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Adds space between buttons
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.updateLoggedFood(
                                    weight = amount.toFloat(),
                                    category = catSelectedOption,
                                    ss = ssSelectedOption,
                                    kcal = (selectedFood?.kcal_100g
                                        ?: 0F) / 100 * (amount.toFloatOrNull() ?: 0F),
                                    id = loggedId.toLong()
                                )
                                navController.navigate("FoodLog?navDate=${date}")
                            },
                            enabled = (amount.toFloatOrNull() ?: 0f) > 0
                        ) {
                            Text("Update")
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            onClick = {
                                showDialog = true
                            }
                        ) {
                            Text("Delete")
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Text("Cancel")
                        }
                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Delete Warning",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                title = {
                                    Text("Delete Entry?")
                                },
                                text = {
                                    Text("Are you sure you want to delete this? This action cannot be undone.")
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showDialog = false
                                            viewModel.deleteLoggedFood(
                                                id = loggedId.toLong()
                                            )
                                            navController.navigate("FoodLog?navDate=${date}")
                                        }
                                    ) {
                                        Text("Delete", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showDialog = false }
                                    ) {
                                        Text("Cancel", color = AlertDialogDefaults.textContentColor)
                                    }
                                }
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
fun FoodAddPreview() {
    val navController = rememberNavController()
    MaterialTheme{
        FoodAdd(date = "2099-12-31",foodId = "", category = "Breakfast", navController = navController)
    }
}