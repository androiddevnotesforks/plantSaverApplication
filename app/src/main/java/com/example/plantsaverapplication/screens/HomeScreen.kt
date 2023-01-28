package com.example.plantsaverapplication.screens

import android.annotation.SuppressLint
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
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.plantsaverapplication.R
import com.example.plantsaverapplication.dialogs.DialogDeletePlants
import com.example.plantsaverapplication.navigation.Route
import com.example.plantsaverapplication.reminder.RemindersManager
import com.example.plantsaverapplication.roomDatabase.Plants
import com.example.plantsaverapplication.roomDatabase.PlantsViewModel
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.util.*

/**
 * Krizbai Csaba - 2022.10.17
 * When the app starts, this will be the main screen
 */

private const val TAG = "HomeScreen"
private const val UNKNOWN_DAY: Int = 0xFF


/**
 * This is the main menu, where you can see the user's plants. This screen provides the ability to edit and delete plants from database.
 * @param navController - We need to navigate to another screen.
 * @param viewModel - Through this we have access to the plants
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: PlantsViewModel
) {
    Log.i(TAG, "User opened Home screen")

    // starts observing this LiveData and represents its values via State.
    val allPlants by viewModel.allProducts.observeAsState(mutableStateListOf())

    var dayRemaining by remember {
        mutableStateOf(UNKNOWN_DAY)
    }

    // Search next watering period (Min. of all plants)
    for (plant in allPlants) {
        val localMin = searchDayRemaining(plant)
        if (localMin != UNKNOWN_DAY && dayRemaining > localMin) {
            dayRemaining = localMin
        }
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
            TopCardHolder(dayRemaining = dayRemaining, plantNumber = allPlants.size)

            // Draw plant list
            PlantsRecycleView(
                plantList = allPlants,
                navController = navController,
                viewModel = viewModel
            )
        }
        // Draw add plant button
        AddPlantButton(navController)
    }
}

/**
 * This function displays data about the plants, like summary
 * @param dayRemaining - how many days remained until watering
 * @param plantNumber - how many plants stored in database
 */
@Composable
private fun TopCardHolder(dayRemaining: Int, plantNumber: Int) {

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
            text = if (dayRemaining == UNKNOWN_DAY) {
                "I could not find any plants!"
            } else {
                "Next watering in $dayRemaining days"
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

        // Number of plants
        Text(
            textAlign = TextAlign.Start,
            fontFamily = FontFamily.Monospace,
            text = "You have $plantNumber plants",
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

/**
 * This function draws the plus button. This allows the user to add a new plant to the database.
 * @param navController - We need to navigate to another screen. (AddPlantScreen.kt)
 */
@Composable
fun AddPlantButton(navController: NavHostController) {
    Log.i(TAG, "Created add plant button!")

    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {


        OutlinedButton(
            onClick = {
                Log.d(TAG, "User cLicked to AddPlant button")
                navController.navigate(Route.ADD_PLANT_NEW)
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
 * @param plantList - We need a list of Plants
 * @param navController - When user clicks on the plant, we need to navigate to another screen. (AddPlantScreen.kt)
 * @param viewModel - This is required to delete the flower, because is stored in viewModel.
 */
@Composable
fun PlantsRecycleView(
    plantList: List<Plants>,
    navController: NavHostController,
    viewModel: PlantsViewModel,
) {

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = plantList
            ) { plant ->
                PlantListItem(
                    plant = plant,
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}

/**
 * This composable is used to display only one plant in recyclerview (In these case, LazyColumn)
 * @param plant - Plant you want to display
 * @param navController - When user clicks on the plant, we need to navigate to another screen. (AddPlantScreen.kt)
 * @param viewModel - This is required to delete the flower, because is stored in viewModel.
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlantListItem(
    plant: Plants,
    navController: NavHostController,
    viewModel: PlantsViewModel
) {

    val mContext = LocalContext.current
    val dismissState = rememberDismissState()
    val composableScope = rememberCoroutineScope()

    // Check dismissState change. When user dismissed plant, delete from database
    if (dismissState.currentValue == DismissValue.DismissedToStart) {
        if (dismissState.isDismissed(DismissDirection.EndToStart)) {
            DialogDeletePlants(plant) { userAnswer ->
                // Check user answer
                if (userAnswer) {
                    Log.i(TAG, "User deleted plant with id:${plant.id}")
                    // Delete plant from room database!
                    viewModel.deletePlant(plant.id!!)
                    // We will no longer need this warning.
                    RemindersManager.stopReminder(
                        context = mContext,
                        reminderId = plant.id!!
                    )
                }
                // Restore dismiss state when answer received
                composableScope.launch {
                    dismissState.reset()
                }
            }
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
                            Log.i(TAG, "User Clicked to plant ${plant.name}, ID:${plant.id}")
                            navController.navigate(
                                Route.ADD_PLANT_UPDATE.replace(
                                    oldValue = "{plant}",
                                    newValue = plant.id.toString()
                                )
                            )
                        }
                ) {
                    // Image
                    PlantImage(image = plant.image!!.asImageBitmap())

                    Column(
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxHeight()
                    ) {
                        // Plant name
                        PlantText(plantName = plant.name!!, plantDesc = plant.description!!)

                        // Selected days
                        SelectedDays(selectedDays = plant.sprayingCycle)

                        // Time of alarms
                        Text(text = plant.time.toString(), style = typography.body1)
                    }

                    // How many days until the next watering
                    searchDayRemaining(plant).let { daysLeft ->
                        if (daysLeft != UNKNOWN_DAY) {
                            DaysLeftText(daysRemaining = daysLeft.toString())
                        }
                    }
                }
            }
        }
    )
}

/**
 *  This Composable function draws image at le start of the recyclerview card.
 *  !!! It would be nice to optimize the image size.
 *  @param image - We need bitmap from image
 */
@Composable
private fun PlantImage(image: ImageBitmap) {
    Image(
        bitmap = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(8.dp)
            .size(110.dp)
            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
    )
}

/**
 * This Composable function draws plant name and description label at the center of the recyclerview card.
 * @param plantName - Plant name as String
 * @param plantDesc - Plant description as String
 */
@Composable
private fun PlantText(plantName: String, plantDesc: String) {
    Text(text = plantName, style = typography.h6)
    Text(text = plantDesc, style = typography.caption)
}

/**
 * Draw for the user which day they have chosen.
 * @param selectedDays - Selected days as binary number. MSB is monday!
 */
@Composable
private fun SelectedDays(selectedDays: Int?) {
    Row {
        for ((dayIndex, dayName) in listOf("M", "T", "W", "T", "F", "S", "S").withIndex()) {
            // Set background color
            var selectedColor = Color.Transparent
            if (selectedDays!!.shr(6 - dayIndex).and(1) != 0) {
                selectedColor = MaterialTheme.colorScheme.onSecondaryContainer
            }
            // Draw days
            Box(
                modifier = Modifier
                    .padding(end = 5.dp, top = 10.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
                    .padding(horizontal = 5.dp)
            ) {
                Text(text = dayName)
            }
        }
    }
}

/**
 * This Composable function draws how many days left on the end of the recyclerview.
 * @param daysRemaining - how many days remaining
 */
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

/**
 * Find out how many days are left until the next chatter.
 * @param plant - Looking for the minimum days of this flower
 */
private fun searchDayRemaining(plant: Plants): Int {
    // Days remaining
    val currentDate = DateTime().dayOfWeek().get() - 1

    // Convert plant spraying cycle to calendar date
    var daysLeft = UNKNOWN_DAY

    // 0 - Monday, 7 - Sunday
    for (day in 0..7) {
        if (plant.sprayingCycle!!.shr(6 - day).and(1) != 0) {
            val tmp = if (currentDate > day) {
                (7 - currentDate) + day
            } else {
                day - currentDate
            }
            if (tmp < daysLeft) {
                daysLeft = tmp
            }
        }
    }

    return daysLeft
}

/** Always place the preview at the bottom! */
@Composable
@Preview
fun HomeScreenPreview() {
//    PlantSaverApplicationTheme {
//        HomeScreen(rememberNavController())
//    }

//    PlantSaverApplicationTheme {
//        val bit = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
//        val plant = Plants(2, DateTime().millis, 0, "12:22", "Tulipans", "I dont like it", bit)
//        PlantListItem(plant = plant) {
//
//        }
//    }
}
