package com.example.test

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.test.data.NutrtionData.NutritionViewModel
import kotlinx.coroutines.newFixedThreadPoolContext


// the USDA food nutrition data is based on 100g,
// Here I let the user add their own in, I think it more intuitive that
// they put in the serving size, and the nutritional info is based on that serving size
// since everything uses 100g, will need to normalise this before inputing
@Composable
fun UserFoodAdd(
    viewModel: NutritionViewModel = viewModel(),
    navController: NavController
){

    var newData by remember { mutableStateOf(newFoodData())}
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        topBar(
            title = "Create Food",
            onBackClick = dropUnlessResumed {
                navController.popBackStack()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                Text(text = "Food Name (Required)")

                Spacer(modifier = Modifier.height(8.dp))

                BasicTextField(
                    value = newData.description,
                    onValueChange = { newData = newData.copy(description = it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            NUmberField(
                fieldName = "Serving Size (g) (Required)",
                amount = newData.servingSize,
                onAmountChange = { newData = newData.copy(servingSize = it)}
            )


            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Nutritional Information per ${newData.servingSize}g")
            Spacer(modifier = Modifier.height(8.dp))

            NUmberField(
                fieldName = "Protein(g) (Required)",
                amount = newData.protein,
                onAmountChange = { newData = newData.copy(protein = it)}
            )

            Spacer(modifier = Modifier.height(8.dp))

            NUmberField(
                fieldName = "Total Fats(g) (Required)",
                amount = newData.totalLipid,
                onAmountChange = { newData = newData.copy(totalLipid = it)}
            )

            Spacer(modifier = Modifier.height(8.dp))

            NUmberField(
                fieldName = "Carbohydrates(g) (Required)",
                amount = newData.carbs,
                onAmountChange = { newData = newData.copy(carbs = it)}
            )

            Spacer(modifier = Modifier.height(8.dp))

            NUmberField(
                fieldName = "Energy(Kcal) (Required)",
                amount = newData.kcal,
                onAmountChange = { newData = newData.copy(kcal = it)}
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                NUmberField(
                    fieldName = "Sugars(g)",
                    amount = newData.sugars,
                    onAmountChange = { newData = newData.copy(sugars = it)},
                    modifier = Modifier.weight(1f)
                )

                NUmberField(
                    fieldName = "Fiber(g)",
                    amount = newData.fiber,
                    onAmountChange = { newData = newData.copy(fiber = it)},
                    modifier = Modifier.weight(1f)
                )

            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                NUmberField(
                    fieldName = "Calcium(g)",
                    amount = newData.calcium,
                    onAmountChange = { newData = newData.copy(calcium = it)},
                    modifier = Modifier.weight(1f)
                )
                NUmberField(
                    fieldName = "Iron(g)",
                    amount = newData.iron,
                    onAmountChange = { newData = newData.copy(iron = it)},
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){

                NUmberField(
                    fieldName = "Potassium(g)",
                    amount = newData.potassium,
                    onAmountChange = { newData = newData.copy(potassium = it)},
                    modifier = Modifier.weight(1f)
                )

                NUmberField(
                    fieldName = "Sodium(g)",
                    amount = newData.sodium,
                    onAmountChange = { newData = newData.copy(sodium = it)},
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                NUmberField(
                    fieldName = "Vitamin A(mg)",
                    amount = newData.vitaminA,
                    onAmountChange = { newData = newData.copy(vitaminA = it)},
                    modifier = Modifier.weight(1f)
                )

                NUmberField(
                    fieldName = "Vitamin C(mg)",
                    amount = newData.vitaminC,
                    onAmountChange = { newData = newData.copy(vitaminC = it)},
                    modifier = Modifier.weight(1f)
                )


            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                NUmberField(
                    fieldName = "Thiamin(mg)",
                    amount = newData.thiamin,
                    onAmountChange = { newData = newData.copy(thiamin = it)},
                    modifier = Modifier.weight(1f)
                )

                NUmberField(
                    fieldName = "Niacin(mg)",
                    amount = newData.niacin,
                    onAmountChange = { newData = newData.copy(niacin = it)},
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){


                NUmberField(
                    fieldName = "Folate(mg)",
                    amount = newData.folate,
                    onAmountChange = { newData = newData.copy(folate = it)},
                    modifier = Modifier.weight(1f)
                )

                NUmberField(
                    fieldName = "Cholesterol(mg)",
                    amount = newData.cholesterol,
                    onAmountChange = { newData = newData.copy(cholesterol = it)},
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                NUmberField(
                    fieldName = "Trans Fat(g)",
                    amount = newData.transFat,
                    onAmountChange = { newData = newData.copy(transFat = it)},
                    modifier = Modifier.weight(1f)
                )

                NUmberField(
                    fieldName = "Saturated Fat(g)",
                    amount = newData.saturatedFat,
                    onAmountChange = { newData = newData.copy(saturatedFat = it)},
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = dropUnlessResumed {
                        //TODO add create new food
                        navController.popBackStack()
                    },
                    enabled = (newData.description != "") &&
                            (newData.servingSize != "") &&
                            (newData.protein != "") &&
                            (newData.totalLipid != "") &&
                            (newData.carbs != "") &&
                            (newData.kcal != "")
                ) {
                    Text("Create")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = dropUnlessResumed {
                        navController.popBackStack()
                    }
                ) {
                    Text("Cancel")
                }
            }

        }
    }
}


@Composable
fun NUmberField(
    fieldName: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    )
        {
            Text(text = fieldName)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        onAmountChange(newValue)
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                placeholder = { Text("0") })
        }
}

data class newFoodData(
    var description: String = "",
    var servingSize: String = "",
    var protein: String = "",
    var totalLipid: String = "",
    var carbs: String = "",
    var kcal: String = "",
    var sugars: String = "",
    var fiber: String = "",
    var calcium: String = "",
    var iron: String = "",
    var potassium: String = "",
    var sodium: String = "",
    var vitaminA: String = "",
    var vitaminC: String = "",
    var thiamin: String = "",
    var niacin: String = "",
    var folate: String = "",
    var cholesterol: String = "",
    var transFat: String = "",
    var saturatedFat: String = ""
)