package com.example.plantsaverapplication.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.plantsaverapplication.R
import com.example.plantsaverapplication.navigation.Destination
import com.example.plantsaverapplication.reminder.RemindersManager
import com.example.plantsaverapplication.roomDatabase.DatabaseHandler
import com.example.plantsaverapplication.roomDatabase.Plants
import com.example.plantsaverapplication.ui.theme.PlantSaverApplicationTheme
import org.joda.time.DateTime

/**
 * Krizbai Csaba - 2022.10.17
 * When the app starts, this will be the main screen
 */

private const val TAG = "HomeScreen"

private var plantListFromDb by mutableStateOf(listOf<Plants>())
private var nextWateringDay by mutableStateOf(0xFF)


@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Log.i(TAG, "User opened Home screen")

    // Read plants from database
    val mContext = LocalContext.current
    LaunchedEffect(Unit) {
        plantListFromDb = DatabaseHandler.getInstance(mContext).read()
    }
    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
            .padding(top = 5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Draw green top card
            TopCardHolder()

            // Draw plant list
            PlantsRecycleView(plantListFromDb)
        }

        // Draw add plant button
        AddPlantButton(navController)
    }
}

@Composable
private fun TopCardHolder() {

    /** Top container - with user information */
    ConstraintLayout(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            )
    ) {
        val (dayLeftTitle, img, plantNumberText) = createRefs()

        // Next watering day
        Text(
            textAlign = TextAlign.Left,
            fontFamily = FontFamily.Monospace,
            text = if (nextWateringDay == 0xFF) {
                "I could not find any plants!"
            } else {
                "Next watering in $nextWateringDay days"
            },
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .constrainAs(dayLeftTitle) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .padding(start = 10.dp, top = 10.dp)
        )

        // Image
        Image(
            painter = painterResource(id = R.drawable.plant_in_hand),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .constrainAs(img) {
                    start.linkTo(parent.start)
                    top.linkTo(dayLeftTitle.bottom)
                    bottom.linkTo(parent.bottom)
                }
                .padding(top = 8.dp)
                .fillMaxWidth(0.35F)
        )

        //Plant numbers
        Text(
            textAlign = TextAlign.Start,
            fontFamily = FontFamily.Monospace,
            text = "You have ${plantListFromDb.size} plants",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .constrainAs(plantNumberText) {
                    start.linkTo(img.end)
                    top.linkTo(dayLeftTitle.bottom)
                }
                .padding(top = 15.dp)
        )
    }
}

@Composable
fun AddPlantButton(navController: NavHostController) {

    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {

        OutlinedButton(
            onClick = {
                navController.navigate("AddPlant")
            },
            modifier = Modifier.padding(bottom = 12.dp, end = 10.dp), // position padding
            shape = CircleShape,
            elevation = ButtonDefaults.elevation(10.dp),
            contentPadding = PaddingValues(14.dp), // content padding
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondaryContainer),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary,
                backgroundColor = MaterialTheme.colorScheme.onSecondary,
            ),
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "addPlant",
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(style = MaterialTheme.typography.titleMedium, text = "Add plant")
        }
    }
}

/**
 * You can found RecyclerView components below
 * modification started at 2022.11.16
 */

/**
 * RecyclerView components
 */
@Composable
fun PlantsRecycleView(plantList: List<Plants>) {

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = plantList,
                itemContent = {
                    PlantListItem(plant = it)
                }
            )
        }
    }
}

/** One recycler view element holder */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlantListItem(plant: Plants) {

    val dismissState = rememberDismissState()
    val mContext = LocalContext.current


    // Check dismissState change. When user dismissed plant, delete from database
    if (dismissState.isDismissed(DismissDirection.EndToStart)){
        LaunchedEffect(Unit){
            // Refresh next watering counter
            nextWateringDay = 0xFF
            // Delete selected plant
            DatabaseHandler
                .getInstance(mContext)
                .deletePlant(plant.id!!)
            // Remove alert
            RemindersManager.stopReminder(
                context = mContext,
                reminderId = plant.id
            )
            // Refresh plant list
            plantListFromDb = DatabaseHandler
                .getInstance(mContext)
                .read()
        }
    }

    SwipeToDismiss(
        state = dismissState,
        background = {
            Box(
                Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
                    .height(IntrinsicSize.Max)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.error
                            )
                        )
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    modifier = Modifier.padding(end = 15.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Delete Icon",
                )
            }
        },
        directions = setOf(DismissDirection.EndToStart),
        dismissThresholds = { direction ->
            FractionalThreshold(if (direction == DismissDirection.EndToStart) 1f else 0.5f)
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                elevation = 8.dp,
                backgroundColor = Color.Transparent,
                shape = RoundedCornerShape(corner = CornerSize(16.dp)),
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.onPrimary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                        .clickable {
                            //TODO: Create Edit layout
                        }
                ) {
                    // Image
                    PlantImage(image = plant.image!!.asImageBitmap())
                    // Plant name
                    PlantText(plantName = plant.name!!, plantDesc = plant.description!!)

                    // Days remaining
                    val currentDate = DateTime().dayOfWeek().get() - 1


                    // Convert plant spraying cycle to calendar date
                    var minDay = 0xFF

                    // 0 - Monday, 7 - Sunday
                    for (day in 0..7) {
                        if (plant.sprayingCycle!!.shr(6 - day).and(1) != 0) {
                            val tmp = if (currentDate > day) {
                                (7 - currentDate) + day
                            } else {
                                day - currentDate
                            }
                            if (tmp < minDay) {
                                minDay = tmp
                            }
                        }
                    }

                    if (minDay != 0xFF) {
                        DaysLeftText(daysRemaining = minDay.toString())
                    }

                    //Search next watering day
                    if (nextWateringDay > minDay) {
                        nextWateringDay = minDay
                    }
                }
            }
        }
    )
}

/** This Composable function draws image at le start of the recycler view card. */
@Composable
private fun PlantImage(image: ImageBitmap) {
    Image(
        bitmap = image, // painter = painterResource(id = R.drawable.plant_in_hand), // TODO: change this hardcoded value
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(8.dp)
            .size(110.dp)
            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
    )
}

/** This Composable function draws plant name and description label at the center of the recycler view card. */
@Composable
private fun PlantText(plantName: String, plantDesc: String) {
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxHeight()
    ) {
        Text(text = plantName, style = typography.h6)
        Text(text = plantDesc, style = typography.caption)
    }
}

/** This Composable function draws text at the end of the recycler view card. */
@Composable
private fun DaysLeftText(daysRemaining: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(50.dp)
                .padding(end = 10.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            // Days
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                text = daysRemaining
            )
            // Days left
            Text(
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                text = "days left"
            )
        }
    }
}

/** Always place the preview at the bottom! */
@Composable
@Preview
fun HomeScreenPreview() {
    PlantSaverApplicationTheme {
        HomeScreen(rememberNavController())
    }
}