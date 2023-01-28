package com.example.plantsaverapplication.screens

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.plantsaverapplication.R
import com.example.plantsaverapplication.roomDatabase.DatabaseHandler
import com.example.plantsaverapplication.roomDatabase.Plants
import com.example.plantsaverapplication.ui.theme.PlantSaverApplicationTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Krizbai Csaba - 2022.10.17
 * Application settings can be found here
 */

private const val TAG = "SettingsScreen"

@Composable
fun SettingsScreen() {
    Log.i(TAG, "User opened Setting screen")

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val context = LocalContext.current

        Text(
            text = "Settings screen",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable {

                val one = Plants(
                    id = null,
                    date = Calendar.getInstance().time.time,
                    sprayingCycle = 0,
                    time = "${Calendar.getInstance()[Calendar.HOUR_OF_DAY]}:${Calendar.getInstance()[Calendar.MINUTE]}",
                    name = "1",
                    description = "1",
                    image = getBitmapFromImage(context, R.drawable.add_plant_default_0,)
                )

                val two = Plants(
                    id = null,
                    date = Calendar.getInstance().time.time,
                    sprayingCycle = 0,
                    time = "${Calendar.getInstance()[Calendar.HOUR_OF_DAY]}:${Calendar.getInstance()[Calendar.MINUTE]}",
                    name = "2",
                    description = "2",
                    image = getBitmapFromImage(context, R.drawable.add_plant_default_0,)
                )

                val three = Plants(
                    id = null,
                    date = Calendar.getInstance().time.time,
                    sprayingCycle = 0,
                    time = "${Calendar.getInstance()[Calendar.HOUR_OF_DAY]}:${Calendar.getInstance()[Calendar.MINUTE]}",
                    name = "3",
                    description = "3",
                    image = getBitmapFromImage(context, R.drawable.add_plant_default_0,)
                )


                GlobalScope.launch {
                    DatabaseHandler.getInstance(context).insertPlants(one)
                    DatabaseHandler.getInstance(context).insertPlants(two)
                    DatabaseHandler.getInstance(context).insertPlants(three)
                }

            }
        )
    }
}

@Composable
@Preview
fun SettingsScreenPreview() {
    PlantSaverApplicationTheme {
        SettingsScreen()
    }
}