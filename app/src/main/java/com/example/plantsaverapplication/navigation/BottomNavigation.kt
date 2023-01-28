package com.example.plantsaverapplication.navigation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.plantsaverapplication.roomDatabase.PlantsViewModel
import com.example.plantsaverapplication.screens.AddPlantScreen
import com.example.plantsaverapplication.screens.HomeScreen
import com.example.plantsaverapplication.screens.SettingsScreen
import com.example.plantsaverapplication.screens.TableScreen

/**
 * Krizbai Csaba - 2022.11.12
 * This class is responsible for displaying the bottom navigation.
 */

private const val TAG = "BottomNavigation"

@Composable
fun DrawBottomNavigation(navController: NavHostController, viewModel: PlantsViewModel) {

    val hideBottomBar = navController.currentBackStackEntryAsState().value?.destination?.route?.startsWith("AddPlant") == false

    Log.i(TAG, "Bottom navigation is visible status is: $hideBottomBar!")

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = hideBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                CustomBottomNavigationView(navController = navController)
            }

        },
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) { paddingValues ->
        Column(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {

            BottomNavigation(
                navController = navController,
                viewModel = viewModel
            )

        }
    }
}

/**
 *  When the user clicks on the bottom navigation, the route to the destination is evaluated here.
 *  @param navController - as NavHostController
 *  @param viewModel - HomeScreen and AddPlantScreen using thise viewmodel
 */
@Composable
fun BottomNavigation(
    navController: NavHostController,
    viewModel: PlantsViewModel,
) {
    NavHost(navController = navController, startDestination = Route.HOME) {

        composable(route = Route.HOME) {
            HomeScreen(navController, viewModel = viewModel)
        }

        composable(route = Route.TABLE) {
            TableScreen()
        }

        composable(route = Route.SETTINGS) {
            SettingsScreen()
        }

        composable(route = Route.ADD_PLANT_UPDATE) {
            Log.d(TAG, "Entered to secondary composable!")

            val openThisID = navController.currentBackStackEntry?.arguments?.getString("plant") ?: "newID"

            AddPlantScreen(
                navController = navController,
                plantID = openThisID,
                viewModel = viewModel
            )
        }
    }
}