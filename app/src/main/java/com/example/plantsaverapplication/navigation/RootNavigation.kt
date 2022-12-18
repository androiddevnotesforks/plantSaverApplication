package com.example.plantsaverapplication.navigation

import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.plantsaverapplication.screens.SplashScreen


@Composable
fun RootNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Destination.SPLASH
    ) {

       composable(route = Destination.SPLASH){
           SplashScreen {
               navController.popBackStack()
               navController.navigate(Destination.MAIN)
           }
       }

        composable(route = Destination.MAIN) {
            MainScreen()
        }
    }
}


object Destination {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val SLAVE = "slave"
}