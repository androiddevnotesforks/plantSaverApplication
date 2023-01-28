package com.example.plantsaverapplication.screens

import android.util.Log
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
 * TODO: Find out what other features you need
 */

private const val TAG = "TableScreen"

@Composable
fun TableScreen() {

    Log.i(TAG, "User opened Table screen")

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Table screen",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
@Preview
fun TableScreenPreview() {
    PlantSaverApplicationTheme {
        TableScreen()
    }
}