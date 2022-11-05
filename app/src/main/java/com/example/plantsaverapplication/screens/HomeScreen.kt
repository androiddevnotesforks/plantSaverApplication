package com.example.plantsaverapplication.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.plantsaverapplication.ui.theme.PlantSaverApplicationTheme

/**
 * Krizbai Csaba - 2022.10.17
 * When the app starts, this will be the main screen
 */

private var TAG = "HomeScreen"

@Composable
fun HomeScreen() {
    Log.i(TAG, "User opened Home screen")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Home screen",
            style = MaterialTheme.typography.bodyMedium
        )

    }
}

@Composable
@Preview
fun HomeScreenPreview(){
    PlantSaverApplicationTheme {
        HomeScreen()
    }
}