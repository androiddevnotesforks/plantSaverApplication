package com.example.plantsaverapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plantsaverapplication.roomDatabase.PlantsViewModel
import com.example.plantsaverapplication.screens.SplashScreen


@Composable
fun RootNavigation(navController: NavHostController, viewModel: PlantsViewModel) {
    NavHost(
        navController = navController,
        startDestination = Destination.SPLASH
    ) {

        // Splash screen
       composable(route = Destination.SPLASH){
           SplashScreen {
               navController.popBackStack()
               navController.navigate(Destination.MAIN)
           }
       }

        // After the Splash screen we will displayed this
        composable(route = Destination.MAIN) {
            DrawBottomNavigation(navController = rememberNavController(), viewModel)
        }
    }
}


object Destination {
    const val SPLASH = "splash"
    const val MAIN = "main"
}

object Route {
    const val HOME = "home"
    const val TABLE = "table"
    const val SETTINGS = "settings"
    const val ADD_PLANT_UPDATE = "AddPlant/plant={plant}"
    const val ADD_PLANT_NEW = "AddPlant/plant=newID"
}