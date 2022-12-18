
package com.example.plantsaverapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.plantsaverapplication.navigation.RootNavigation
import com.example.plantsaverapplication.ui.theme.PlantSaverApplicationTheme

/**
 * Krizbai Csaba
 * MainActivity
 */
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlantSaverApplicationTheme(
                content = {
                    RootNavigation(navController =  rememberNavController())
                }
            )
        }
    }
}