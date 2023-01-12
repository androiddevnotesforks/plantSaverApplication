package com.example.plantsaverapplication.navigation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.plantsaverapplication.screens.AddPlantScreen
import com.example.plantsaverapplication.screens.HomeScreen
import com.example.plantsaverapplication.screens.SettingsScreen
import com.example.plantsaverapplication.screens.TableScreen


private const val TAG = "MainNavigation"

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {

    Log.d(TAG, "Main navigation is loaded!")

    val navigationVisibility = remember { MutableTransitionState(true) }
        .apply { targetState = false }


    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visibleState = navigationVisibility,
                enter = slideInVertically(initialOffsetY = { it }),
                exit =  slideOutVertically(targetOffsetY = { it })
            ) {
                CustomBottomNavigation(navController = navController)
            }

        },
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) { paddingValues ->
        Column(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
            MainNavigation(navController = navController) {
                navigationVisibility.targetState = it
            }
        }
    }
}

@Composable
fun MainNavigation(navController: NavHostController, navigationVisibility: (Boolean) -> (Unit)) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            navigationVisibility(true)
            HomeScreen(navController)
        }

        composable("table") {
            navigationVisibility(true)
            TableScreen()
        }

        composable("settings") {
            navigationVisibility(true)
            SettingsScreen()
        }

        secondaryHomeNavigation(navController = navController, navigationVisibility)
    }
}


fun NavGraphBuilder.secondaryHomeNavigation(
    navController: NavHostController,
    navigationVisibility: (Boolean) -> Unit
) {
    navigation(
        route = Destination.SLAVE,
        startDestination = "addPlant"
    ) {
        composable(route = "addPlant") {
            navigationVisibility(false)
            // TODO: https://stackoverflow.com/questions/67516606/jetpack-compose-how-to-wait-for-animation-ends
            AddPlantScreen(navController = navController)
        }
    }
}