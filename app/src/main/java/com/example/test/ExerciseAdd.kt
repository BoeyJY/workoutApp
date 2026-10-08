package com.example.test

import android.R.attr.onClick
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CommentBank
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.test.data.ExerciseViewModel
import com.example.test.data.sessionSets


@Composable
fun ExerciseAdd(
                viewModel: ExerciseViewModel = viewModel(),
                date: String,
                exerciseName: String,
                navController: NavController
){
    var weight by remember { mutableStateOf(20f) }
    var reps by remember { mutableStateOf(10) }
    var selectedRow by remember { mutableStateOf<Int?>(null) }

    var setComment by remember { mutableStateOf<sessionSets?>(null) }
    var tempComment by remember { mutableStateOf("") }
//    var sets by remember { mutableStateOf(listOf<sessionSets>()) }
//
//    var existing = viewModel.getSessionSets(date, exerciseName)
//
//    if (existing != null){
//        sets = existing
//    }

//    var sets = viewModel.sets
//    LaunchedEffect(date, exerciseName) {
//        viewModel.getSessionSets(date, exerciseName)
//    }
    val sets by viewModel
        .getSessionSets(date, exerciseName)
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        topBar(
            title = "Add ${exerciseName}",
            onBackClick = {
                navController.popBackStack()}
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            NumberInputRow(
                label = "Weight",
                value = weight.toString(),
                unit = "kg",
                onDecrease = {
                    weight = (weight - 2.5f).coerceAtLeast(0f)
                },
                onIncrease = {
                    weight += 2.5f
                },
                onValueChange = {
                    weight = it.toFloatOrNull() ?: weight
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NumberInputRow(
                label = "Reps",
                value = reps.toString(),
                onDecrease = {
                    reps = (reps - 1).coerceAtLeast(1)
                },
                onIncrease = {
                    reps++
                },
                onValueChange = {
                    reps = it.toIntOrNull() ?: reps
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedRow == null) {
                Button(
                    onClick = {
                        viewModel.addSet(date, exerciseName, null, weight, reps)
                    },
                    enabled = reps > 0
                ) {
                    Text("Add")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ){
                    Button(
                        onClick = {
                            val selectedSet = sets[selectedRow!!]
                            viewModel.updateSessionSet(selectedSet.id, weight, reps)
                            selectedRow = null
                        },
                        modifier = Modifier.weight(1f)
                    ){
                        Text("Update")
                    }

                    Button(
                        onClick = {
                            val selectedSet = sets[selectedRow!!]
                            viewModel.deleteSessionSet(selectedSet.id)
                            selectedRow = null
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ){
                        Text("Delete")
                    }

                    Button(
                        onClick = {
                            val selectedSet = sets[selectedRow!!]
                            weight = selectedSet.weight
                            reps = selectedSet.reps
                            selectedRow = null
                        },
                        modifier = Modifier.weight(1f)
                    ){
                        Text("Cancel")
                    }
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "",
                    modifier = Modifier.weight(0.25f)
//                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Set",
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Weight",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Reps",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(sets) { index, set ->
                    SetRow(set = set,
                        isSelected = index == selectedRow,
                        onClick = {
                            selectedRow = index
                            weight = set.weight
                            reps = set.reps
                        },
                        onCommentClick = {
                            setComment = set
                            tempComment = set.comment?: ""
                        }

                    )
                }
            }
        }
    }
    setComment?.let { set ->
        AlertDialog(
            onDismissRequest = { setComment = null },
            title = { Text("Edit Comment") },
            text = {
                OutlinedTextField(
                    value = tempComment,
                    onValueChange = { tempComment = it },
                    label = { Text("Comment") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateSessionSetComment(set.id, tempComment)
                        setComment = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { setComment = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}




@Composable
fun NumberInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    unit: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.width(80.dp),
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = onDecrease,
            modifier = Modifier.size(48.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("−")
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .width(100.dp)
                .padding(horizontal = 8.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center
            )
        )

        Button(
            onClick = onIncrease,
            modifier = Modifier.size(48.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("+")
        }

        if (unit != null) {
            Text(
                text = unit,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

// TODO add a way to put comments
@Composable
fun SetRow(
    set: sessionSets,
    isSelected: Boolean,
    onClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(
                if (isSelected) Modifier
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onCommentClick,
            modifier = Modifier.weight(0.25f)
        ){
            val tint = if(!set.comment.isNullOrEmpty()){
                MaterialTheme.colorScheme.primary
            } else{
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            }
            Icon(
                imageVector = Icons.Default.CommentBank,
                contentDescription = "Edit comment",
                tint = tint
            )
        }

        Text(
            text = set.setNumber.toString(),
            modifier = Modifier.weight(0.5f),
            textAlign = TextAlign.Center
        )

        Text(
            text = set.weight.toString(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Text(
            text = set.reps.toString(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}



@Preview(showBackground = true)
@Composable
fun ExerciseAddPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        ExerciseAdd(date = "2026-09-30", exerciseName = "push up", navController = navController)
    }
}