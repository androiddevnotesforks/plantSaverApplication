package com.example.plantsaverapplication.dialogs


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.plantsaverapplication.roomDatabase.Plants

/**
 * Krizbai Csaba - 2022.01.26
 * All AlertDialogs can be found in this file.
 */


/**
 * We have to ask, are you sure you want to delete the flower?
 * @param plant - Need some information about the plant
 * @param userAnswer - return user answer: true is yes, false is no
 */
@Composable
fun DialogDeletePlants(plant: Plants, userAnswer: (Boolean) -> Unit){

    // TODO: Change dialog design!
    val openDialog = remember { mutableStateOf(true)  }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                openDialog.value = false
            },
            title = {
                Text(text = "Are you sure?")
            },
            text = {
                Text("Would you like to delete this flower? (${plant.name})")
            },
            confirmButton = {
                Button(

                    onClick = {
                        openDialog.value = false
                        userAnswer(true)
                    }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        userAnswer(false)
                    }) {
                    Text("No")
                }
            }
        )
    }

}