package com.example.plantsaverapplication.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plantsaverapplication.BuildConfig
import com.example.plantsaverapplication.R
import com.example.plantsaverapplication.ui.theme.PlantSaverApplicationTheme
import com.example.plantsaverapplication.ui.theme.splashBackground
import kotlinx.coroutines.delay

/**
 * Krizbai Csaba - 2022.10.27
 * Splash screen.
 */

private const val SPLASH_TIME = 1500L
private const val TEXT_FADE_IN_DURATION = 1500

private const val TAG = "SplashScreen"

@Composable
fun SplashScreen(timePassed: () -> Unit) {
    Log.i(TAG, "Application started...")
    // Draw image
    BottomImage()
    // Draw app name
    ApplicationText()
    // After delay, create navigation view
    LaunchedEffect(Unit) {
        delay(SPLASH_TIME)
        timePassed.invoke()
    }
}

@Composable
private fun ApplicationText() {
    // App name
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.8f)
    ) {


        // Fade in animation
        AnimatedVisibility(
            visibleState = MutableTransitionState(false).apply { targetState = true },
            enter = fadeIn(animationSpec = tween(TEXT_FADE_IN_DURATION)),
        ) {
            Text(
                text = stringResource(R.string.splash_title),
                style = MaterialTheme.typography.displayMedium,
                fontFamily = FontFamily.Monospace
            )
        }
    }

    // Version number
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .padding(
                start = 15.dp,
                bottom = 15.dp
            )
            .fillMaxHeight()
    ) {
        Text(
            text = "V" + BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE.toString(),
            style = MaterialTheme.typography.labelLarge,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun BottomImage() {
    // Background image
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
            .background(splashBackground)
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.splash_background
            ),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
@Preview
fun SplashScreenPreview() {
    PlantSaverApplicationTheme {
        // Draw image
        BottomImage()
        // Draw app name
        ApplicationText()
    }
}