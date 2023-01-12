package com.example.plantsaverapplication

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.plantsaverapplication.navigation.RootNavigation
import com.example.plantsaverapplication.reminder.RemindersManager
import com.example.plantsaverapplication.ui.theme.PlantSaverApplicationTheme
import java.util.*

/**
 * Krizbai Csaba
 * MainActivity
 */
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize notification channel
        RemindersManager.createNotificationsChannels(this)

        setContent {
            PlantSaverApplicationTheme(content = {
                RootNavigation(navController = rememberNavController())
            })
        }

    }

}