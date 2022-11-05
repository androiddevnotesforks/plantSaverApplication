package com.example.plantsaverapplication.bottomNavigation


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.plantsaverapplication.R
import com.example.plantsaverapplication.screens.HomeScreen
import com.example.plantsaverapplication.screens.SettingsScreen
import com.example.plantsaverapplication.screens.TableScreen
import com.example.plantsaverapplication.ui.theme.menu_background
import com.example.plantsaverapplication.ui.theme.menu_background_text
import com.example.plantsaverapplication.ui.theme.menu_selector_background


private const val TAG = "myCustomNavigation"



@Composable
fun CreateNavigationBar(navController: NavHostController){
    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                navController = navController,
                onItemSelected = {
                    navController.navigate(it.route)
                }
            )
        },
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Navigation(navController = navController)
    }
}



@Composable
fun CustomBottomNavigation(navController: NavController, onItemSelected:(BottomNavItem)->Unit) {

    /** Create a navigation list */
    val items = listOf(
        BottomNavItem(
            name = "Table",
            route = "table",
            image = Icons.Default.List
        ),
        BottomNavItem(
            name = "Home",
            route = "home",
            image = Icons.Default.Home
        ),
        BottomNavItem(
            name = "Settings",
            route = "settings",
            image = Icons.Default.Settings
        )
    )


    /** We need space from the edge of the screen */
    Box(modifier = Modifier
        .padding(20.dp))
    {

        /** Constraint Layout */
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(menu_background)
            .padding(1.dp))
        {

            val (item1, item2, item3) = createRefs()
            val backStackEntry = navController.currentBackStackEntryAsState()


            /** Save last selected state */
            var lastState by remember {
                mutableStateOf(0.0f)
            }


            /** Move selection box to the selected index */
            Box {
                Log.i(TAG, "Selected menu ${navController.currentDestination?.route}")

                when(backStackEntry.value?.destination?.route){
                    items[0].route ->{
                        MoveSelectionBox(fromState = lastState, toState = 0.135f)
                        lastState = 0.135f
                    }
                    items[1].route ->{
                        MoveSelectionBox(fromState = lastState, toState = 0.5f)
                        lastState = 0.5f
                    }
                    items[2].route ->{
                        MoveSelectionBox(fromState = lastState, toState = 0.87f)
                        lastState = 0.87f
                    }
                }
            }


            /** Create navigation first item.  */
            NavigationItemHolder(
                item = items[0],
                color = menu_background_text,
                isSelected = items[0].route == backStackEntry.value?.destination?.route,
                onItemSelected = onItemSelected,
                modifier = Modifier
                    .constrainAs(item1){
                        start.linkTo(parent.start)
                        end.linkTo(item2.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )

            /** Create Menu second item */
            NavigationItemHolder(
                item = items[1],
                color = menu_background_text,
                isSelected = items[1].route == backStackEntry.value?.destination?.route,
                onItemSelected = onItemSelected,
                modifier = Modifier
                    .constrainAs(item2){
                        start.linkTo(item1.end)
                        end.linkTo(item3.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )

            /** Create Menu third items */
            NavigationItemHolder(
                item = items[2],
                color = menu_background_text,
                isSelected = items[2].route == backStackEntry.value?.destination?.route,
                onItemSelected = onItemSelected,
                modifier = Modifier
                    .constrainAs(item3){
                        start.linkTo(item2.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            )

            /** Create horizontal chain between navigation items */
            createHorizontalChain(
                item1,
                item2,
                item3,
                chainStyle = ChainStyle.Spread
            )
        }
    }
}


@OptIn(ExperimentalMotionApi::class)
@Composable
private fun MoveSelectionBox(fromState: Float, toState :Float) {

    val context = LocalContext.current

    val motionScene = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }

    val motionLayoutProgress = remember {
        Animatable(fromState)
    }

    LaunchedEffect(Unit) {
        motionLayoutProgress.animateTo(
            toState,
            animationSpec = tween(
                durationMillis = 700,
                easing = LinearOutSlowInEasing
            )
        )
    }

    MotionLayout(
        motionScene = MotionScene(content = motionScene),
        progress = motionLayoutProgress.value,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .layoutId("box")
                .clip(RoundedCornerShape(13.dp))
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(menu_selector_background)
            )
        }
    }
}


@Composable
fun NavigationItemHolder(
    item: BottomNavItem,
    color: Color,
    isSelected: Boolean,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier
){
    IconButton(
        onClick = {
            if(!isSelected)
                onItemSelected(item)
            },
        modifier = modifier
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = item.image,
                contentDescription = item.name,
                tint=color
            )
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = item.name,
                    textAlign = TextAlign.Center,
                    color = menu_selector_background,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }
    }

}


@Composable
fun Navigation(navController: NavHostController){
    NavHost(navController = navController, startDestination = "home"){

        Log.i(TAG, "Loading a fragment: ${navController.currentDestination?.route}")

        composable("home"){
            HomeScreen()
        }
        composable("table"){
            TableScreen()
        }
        composable("settings"){
            SettingsScreen()
        }
    }
}

