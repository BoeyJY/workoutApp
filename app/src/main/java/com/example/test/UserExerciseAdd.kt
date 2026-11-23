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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.test.data.ExerciseViewModel
import kotlinx.coroutines.flow.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserExerciseAdd(
    viewModel: ExerciseViewModel = viewModel(),
    navController: NavController
) {

    var name by remember { mutableStateOf("") }

    var primaryExpanded by remember { mutableStateOf(false)}
    var primarySelected by remember { mutableStateOf("")}
    val primaryOptions by produceState<List<String>>(emptyList()) {
        value = viewModel.getDistinctMuscle()
    }

    var secondaryExpanded by remember { mutableStateOf(false)}
    var secondarySelected by remember { mutableStateOf(listOf<String>())}
//    var secondaryOptions = primaryOptions.filter{
//
//    }

    var categoryExpanded by remember { mutableStateOf(false)}
    var categorySelected by remember { mutableStateOf("")}
    val categoryOptions by produceState<List<String>>(emptyList()) {
        value = viewModel.getExerciseCategory()
    }

    var forceExpanded by remember { mutableStateOf(false)}
    var forceSelected by remember { mutableStateOf("")}
    val forceOptions by produceState<List<String>>(emptyList()) {
        value = listOf("") + viewModel.getExerciseForce()
    }

    var levelExpanded by remember { mutableStateOf(false)}
    var levelSelected by remember { mutableStateOf("")}
    val levelOptions by produceState<List<String>>(emptyList()) {
        value = listOf("") + viewModel.getExerciseLevel()
    }

    var mechanicExpanded by remember { mutableStateOf(false)}
    var mechanicSelected by remember { mutableStateOf("")}
    val mechanicOptions by produceState<List<String>>(emptyList()) {
        value = listOf("") + viewModel.getExerciseMechanic()
    }

    var equipmentExpanded by remember { mutableStateOf(false)}
    var equipmentSelected by remember { mutableStateOf("")}
    val equipmentOptions by produceState<List<String>>(emptyList()) {
        value = listOf("") + viewModel.getExerciseEquipment()
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        topBar(
            title = "Create Exercise",
            onBackClick = dropUnlessResumed {
                navController.popBackStack()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ){
            Column {
                Text(text = "Exercise Name (Required)")

                Spacer(modifier = Modifier.height(8.dp))

                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }



            Spacer(modifier = Modifier.height(8.dp))

            DropDown(
                fieldName = "Category (Required)",
                expanded = categoryExpanded,
                onExpandedChange = {categoryExpanded = it},
                selected = categorySelected,
                onSelectedChange = {categorySelected = it},
                options = categoryOptions
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropDown(
                fieldName = "Primary Muscle (Required)",
                expanded = primaryExpanded,
                onExpandedChange = {primaryExpanded = it},
                selected = primarySelected,
                onSelectedChange = {primarySelected = it},
                options = primaryOptions
            )

            val secondaryOptions = listOf("") + primaryOptions.filter{
                it !in secondarySelected && it !in primarySelected
            }
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Secondary Muscles")

                secondarySelected.forEach {selected ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = selected,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                    secondarySelected = secondarySelected - selected
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove $selected"
                            )
                        }
                    }
                }

                if (secondaryOptions.isNotEmpty()){
                    ExposedDropdownMenuBox(
                        expanded = secondaryExpanded,
                        onExpandedChange = {secondaryExpanded = !secondaryExpanded},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = {
                                Text("Select an option")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = secondaryExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = secondaryExpanded,
                            onDismissRequest = {secondaryExpanded = false}
                        ) {
                            secondaryOptions.forEach{ option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }, onClick = {
                                        secondarySelected = secondarySelected + option
                                        secondaryExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding

                                )
                            }
                        }
                    }
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            DropDown(
                fieldName = "Force",
                expanded = forceExpanded,
                onExpandedChange = {forceExpanded = it},
                selected = forceSelected,
                onSelectedChange = {forceSelected = it},
                options = forceOptions
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropDown(
                fieldName = "Level",
                expanded = levelExpanded,
                onExpandedChange = {levelExpanded = it},
                selected = levelSelected,
                onSelectedChange = {levelSelected = it},
                options = levelOptions
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropDown(
                fieldName = "Mechanic",
                expanded = mechanicExpanded,
                onExpandedChange = {mechanicExpanded = it},
                selected = mechanicSelected,
                onSelectedChange = {mechanicSelected = it},
                options = mechanicOptions
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropDown(
                fieldName = "Equipment",
                expanded = equipmentExpanded,
                onExpandedChange = {equipmentExpanded = it},
                selected = equipmentSelected,
                onSelectedChange = {equipmentSelected = it},
                options = equipmentOptions
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = dropUnlessResumed {
                        viewModel.addUserExercise(
                            name = name,
                            force = forceSelected,
                            level = levelSelected,
                            mechanic = mechanicSelected,
                            equipment = equipmentSelected,
                            category = categorySelected,
                            primaryMuscles = primarySelected,
                            secondaryMuscles = secondarySelected,
                        )
                        navController.popBackStack()
                    },
                    enabled = (name != "") && (primarySelected != "") && (categorySelected != "")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(
    fieldName: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selected: String,
    onSelectedChange: (String) -> Unit,
    options: List<String>
) {
    Column {
        Text(text = fieldName)

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {onExpandedChange(!expanded)},
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {onExpandedChange(false)}
            ) {
                options.forEach{ option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, onClick = {
                            onSelectedChange(option)
                            onExpandedChange(false)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding

                    )
                }
            }
        }
    }
}