package com.example.plantsaverapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.plantsaverapplication.navigation.RootNavigation
import com.example.plantsaverapplication.reminder.RemindersManager
import com.example.plantsaverapplication.roomDatabase.PlantViewModelFactory
import com.example.plantsaverapplication.roomDatabase.PlantsViewModel
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
            // Lock screen
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)

            PlantSaverApplicationTheme(content = {
                //
                val owner = LocalViewModelStoreOwner.current
                owner?.let { it ->
                    val viewModel: PlantsViewModel = viewModel(it, "PlantsViewModel", PlantViewModelFactory(LocalContext.current.applicationContext as Application))
                    RootNavigation(navController = rememberNavController(), viewModel)
                }

            })
        }
    }

}

/**
 * For the time being, I have no plans to display it in landscape mode.
 * So screen is locked!
 */
@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

