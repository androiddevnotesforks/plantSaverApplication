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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.plantsaverapplication.ui.theme.PlantSaverApplicationTheme

/**
 * Krizbai Csaba - 2022.10.17
 * Application settings can be found here
 */

private var TAG = "SettingsScreen"

@Composable
fun SettingsScreen() {
    Log.i(TAG, "User opened Setting screen")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Settings screen",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
@Preview
fun SettingsScreenPreview(){
    PlantSaverApplicationTheme {
        SettingsScreen()
    }
}