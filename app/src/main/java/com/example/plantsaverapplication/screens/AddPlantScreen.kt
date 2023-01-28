package com.example.plantsaverapplication.screens

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.TimePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.plantsaverapplication.R
import com.example.plantsaverapplication.recyclerViews.Day
import com.example.plantsaverapplication.recyclerViews.DaySelectorRecyclerView
import com.example.plantsaverapplication.reminder.RemindersManager
import com.example.plantsaverapplication.roomDatabase.DatabaseHandler
import com.example.plantsaverapplication.roomDatabase.Plants
import com.example.plantsaverapplication.roomDatabase.PlantsViewModel
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.util.*


/**
 * Krizbai Csaba - 2022.11.29
 * When user click to "Add plant" button, this screen will be appear
 * */

//  TODO: 0. Finish logging
//  TODO: 1. Show some error message if input is not correct
//  TODO: 2. The user should also be able to upload their own image

private var TAG = "AddPlantScreen"

private val defaultImage = listOf (
    R.drawable.add_plant_default_0,
    R.drawable.add_plant_default_1,
    R.drawable.add_plant_default_2,
    R.drawable.add_plant_default_3,
    R.drawable.add_plant_default_4,
    R.drawable.add_plant_default_5,
)

/**
 * To be able to restore the previous screen I need NavHostController.
 * User can close this window if you step back or add a new plant to Room database.
 */
@Composable
fun AddPlantScreen(navController: NavHostController, plantID: String, viewModel: PlantsViewModel) {
    Log.i(TAG, "User opened add plant screen with plantID: $plantID")
    val mContext = LocalContext.current

    // When ID is equal with "newID", its not valid. In these case we need to add new plant
    val newPlant = if(plantID == "newID"){
        Plants(
            id = null,
            date = Calendar.getInstance().time.time,
            sprayingCycle = 0,
            time = "${Calendar.getInstance()[Calendar.HOUR_OF_DAY]}:${Calendar.getInstance()[Calendar.MINUTE]}",
            name = "",
            description = "",
            image = getBitmapFromImage(mContext, defaultImage.random())
        )
    }else{
        val allPlants by viewModel.allProducts.observeAsState(listOf())
        allPlants.find { plants ->
            plants.id!! == plantID.toInt()
        }
    }

    if(newPlant?.name == null){
        Log.d(TAG, "Plant name is null!")
        return
    }

    //  Draw green background
    ImageGreenBackground()

    //  DrawForm
    PlantForm(navController, newPlant, mContext)
}


/**
 * This function drawing green decoration box
 */
@Composable
fun ImageGreenBackground() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.45F)
            .clip(RoundedCornerShape(bottomEnd = 30F, bottomStart = 30F))
            .background(MaterialTheme.colorScheme.onSecondary)
    )
}

/**
 * This composable function summarises the whole form.
 */
@Composable
fun PlantForm(
    navController: NavHostController,
    plant: Plants,
    mContext: Context,
) {

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp, bottom = 25.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Create local Plant. We will save this plant in the database
        var localPlants = plant
        //  Form mutable variables
        var plantNameError by remember { mutableStateOf(false) }
        var plantName by remember { mutableStateOf(TextFieldValue(plant.name.toString())) }
        var plantDescError by remember { mutableStateOf(false) }
        var plantDesc by remember { mutableStateOf(TextFieldValue(plant.description.toString())) }
        var plantPeriodError by remember { mutableStateOf(false) }
        var errorVisibility by remember { mutableStateOf(false) }
        var plantTime by remember { mutableStateOf(plant.time!!) }
        val coroutineScope = rememberCoroutineScope()
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        var selectedDays by rememberSaveable {
            mutableStateOf(
                days.mapIndexed {index, dayName ->
                    Day(
                        dayName = dayName,
                        isSelected = plant.sprayingCycle!!.shr(6 - index).and(1) == 1
                    )
                }
            )
        }




        // Picture of a plant at the top of the screen.
        Image(
            painter = painterResource(id = defaultImage[0]), // TODO: Change this value
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(8.dp)
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(8.dp)
                )
        )

        // Text above the image. This is just screen explanation.
        Text(
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            text = "Add a new plant to your garden",
            color = MaterialTheme.colorScheme.onPrimary,
            fontFamily = FontFamily.Monospace
        )

        // Show error message for user when form input is not correct!
        AnimatedVisibility(visible = errorVisibility) {
            Text(
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                text = "Please fill out all required fields!",
                color = MaterialTheme.colorScheme.error,
                fontFamily = FontFamily.Monospace
            )
        }

        // Column with white background
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9F)
                .fillMaxWidth(0.88F)
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(corner = CornerSize(10.dp)))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            /**  Draw plant name */
            OutlinedEditText(
                headerText= "Plant name",
                hint = "Enter the name",
                imageID = R.drawable.ic_name_64px,
                isError = plantNameError,
                plantName = plantName,
                width = 0.9F
            ) {
                plantName = it
                if (plantNameError) {
                    plantNameError = false
                }
            }

            /** Draw plant description */
            OutlinedEditText(
                headerText = "Description",
                hint = "Leave a comment",
                imageID = R.drawable.ic_edit_64px,
                isError = plantDescError,
                plantName = plantDesc,
                width = 0.9F
            ) {
                plantDesc = it
                if (plantDescError) {
                    plantDescError = false
                }

            }

            /** Draw dropdown for period selector */
            DropDownPeriodSelector(
                selectedDays = selectedDays,
                width = 0.9F,
                focusManager = focusManager,
                error = plantPeriodError,
                onSelectedDay = { ID, state ->
                    selectedDays = selectedDays.mapIndexed { index, item ->
                        if (ID == index) {
                            item.copy(isSelected = state)
                        } else item
                    }
                    // Reset error when user selected something
                    if(plantPeriodError){
                        plantPeriodError = false
                    }

                })

            /** Time picker (Hour and minute) */
            OutlinedTimePicker(
                timeSelected = plantTime,
                imageID = R.drawable.ic_hour_64px,
                width = 0.9F
            ) { _, selHour, selMinute ->
                // Set text value
                plantTime = "$selHour:$selMinute"
            }

        }
        /**  Draw a form save button at the bottom of the view */
        FormSaveButton(onClicked = {

            // Check form status. Empty inputs not allowed!
            if (plantName.text.isEmpty()) {
                Log.d(TAG, "Plant name is empty")
                plantNameError = true
                errorVisibility = true
                return@FormSaveButton
            }

            if (plantDesc.text.isEmpty()) {
                Log.d(TAG, "Plant description is empty")
                plantDescError = true
                errorVisibility = true
                return@FormSaveButton
            }

            coroutineScope.launch {
                var binaryNumberAsInt = 0b0
                // We need to convert list value to int
                // MSB is Monday!!!
                for ((index, day) in selectedDays.withIndex()) {
                    if (day.isSelected) {
                        binaryNumberAsInt = binaryNumberAsInt.or(0b1)
                    }
                    if (index != 6) {
                        binaryNumberAsInt = binaryNumberAsInt.shl(1)
                    }
                }

                if(binaryNumberAsInt == 0){
                    Log.d(TAG, "Watering period is not selected")
                    plantPeriodError = true
                    errorVisibility = true
                    return@launch
                }

                Log.d(TAG, "[${plant.id}]${plantName.text} added with $binaryNumberAsInt cycle")

                plant.name = plantName.text
                plant.description = plantDesc.text
                plant.time = plantTime
                plant.sprayingCycle= binaryNumberAsInt


                // Insert new plant to database and load home screen
                val insertedPlantID = DatabaseHandler.getInstance(mContext).insertPlants(plant)
                //viewModel.insertPlant(plant)

                // Create alert and after that load home screen
                for ((index, day) in selectedDays.withIndex()) {
                    if (day.isSelected) {
                        createAlert(
                            context = mContext,
                            indexOfSelectedDay = index,
                            selectedTime = plant.time!!,
                            plantName = plantName.text,
                            plantID = insertedPlantID)
                    }
                }

                navController.popBackStack()
                navController.navigate("home")
            }
        })
    }
}

private fun createAlert(context: Context, indexOfSelectedDay: Int, selectedTime: String, plantName: String, plantID: Long){
    Log.d(TAG, "Alert created with ID:$plantID! Days:$indexOfSelectedDay, name:$plantName")
    // Get current date
    var currentDate = DateTime()
    currentDate = currentDate.withDayOfWeek(indexOfSelectedDay+1)
    // Set selected day
    // Set hour and day and create alert for user
    selectedTime.split(":").apply {
        currentDate = currentDate.withHourOfDay(this[0].toInt())
        currentDate = currentDate.withMinuteOfHour(this[1].toInt())
    }
    // Start reminder
    RemindersManager.startReminder(
        context = context,
        reminderTime = currentDate,
        plantName = plantName,
        plantID = plantID)
}


// on below line we are creating a function to get bitmap
// from image and passing params as context and an int for drawable.
fun getBitmapFromImage(context: Context, drawable: Int): Bitmap {

    // on below line we are getting drawable
    val db = ContextCompat.getDrawable(context, drawable)

    // in below line we are creating our bitmap and initializing it.
    val bit = Bitmap.createBitmap(
        500, 500, Bitmap.Config.ARGB_8888
    )

    // on below line we are
    // creating a variable for canvas.
    val canvas = Canvas(bit)

    // on below line we are setting bounds for our bitmap.
    db!!.setBounds(0, 0, canvas.width, canvas.height)


    // on below line we are simply
    // calling draw to draw our canvas.
    db.draw(canvas)

    // on below line we are
    // returning our bitmap.
    return bit
}



@Composable
fun OutlinedTimePicker(
    timeSelected: String,
    imageID: Int,
    width: Float,
    onTimeSelected: (TimePicker, Int, Int) -> Unit
) {
    // Declaring and initializing a calendar
    val mCalendar = Calendar.getInstance()


    // Creating a TimePicker dialog
    val mTimePickerDialog = TimePickerDialog(
        LocalContext.current,
        R.style.MyTimePickerStyle,
        onTimeSelected,
        mCalendar[Calendar.HOUR_OF_DAY],
        mCalendar[Calendar.MINUTE],
        false,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth(width)
            .padding(top = 15.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(5.dp)
            )
            .clickable {
                mTimePickerDialog.show()
            }
    ) {
        Row(Modifier.padding(vertical = 10.dp)) {
            Image(
                painter = painterResource(id = imageID),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .align(Alignment.CenterVertically)
                    .size(35.dp)
            )

            Text(
                text =
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                        append("Alarm on ")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                        append(timeSelected)
                    }
                },
                modifier = Modifier
                    .padding(10.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
            )
        }


    }

}

@Composable
fun OutlinedEditText(
    headerText: String,
    hint: String,
    imageID: Int,
    isError: Boolean = false,
    plantName: TextFieldValue,
    width: Float,
    onTextChanged: (TextFieldValue) -> Unit
) {
    val colors = TextFieldDefaults.textFieldColors(
        textColor = MaterialTheme.colorScheme.onSurface,
        backgroundColor = Color.Transparent,
        focusedLabelColor = MaterialTheme.colorScheme.onSecondary,
        unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
        focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary
    )

    OutlinedTextField(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(width),
        value = plantName,
        isError = isError,
        singleLine = true,
        keyboardOptions =  KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
        colors = colors,
        leadingIcon = {
            Image(
                painter = painterResource(id = imageID),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(35.dp)
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        onValueChange = onTextChanged,
        placeholder = {
            Text(text = hint, fontFamily = FontFamily.Monospace, maxLines = 1)
        },
        label = {
            Text(text = headerText, fontFamily = FontFamily.Monospace, maxLines = 1)
        },

    )
}

/**
 * In this composable, the user can select the watering period.
 * @param:
 *      selectedDays - We need a list of Days. The Day data class contains the name of the day and that it is selected.
 *      width - dropdown width in float
 *      onSelectedDay - This function handle if user selected a day from list
 */
@Composable
fun DropDownPeriodSelector(
    selectedDays: List<Day>,
    width: Float,
    focusManager: FocusManager,
    error: Boolean,
    onSelectedDay: (Int, Boolean) -> Unit
) {
    var visibility by remember { mutableStateOf(false) }

    val borderColor = if(error){
        MaterialTheme.colorScheme.error
    }else{
        MaterialTheme.colorScheme.tertiary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(width)
            .padding(top = 15.dp)
            .border(
                1.dp,
                borderColor,
                shape = RoundedCornerShape(5.dp)
            )
            .clickable {
                visibility = !visibility
                focusManager.clearFocus()
            }
    ) {
        Row(Modifier.padding(vertical = 10.dp)) {
            Image(
                painter = painterResource(id = R.drawable.ic_date_64px),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .align(Alignment.CenterVertically)
                    .size(35.dp)
            )

            var headerText = ""
            for(daySelected in selectedDays){
                if(daySelected.isSelected){
                    headerText += "${daySelected.dayName[0]} "
                }
            }
            if(headerText == ""){
                headerText = "Watering period"
            }

            Text(
                modifier = Modifier
                    .padding(10.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                text = headerText,
                color = MaterialTheme.colorScheme.tertiary,
                fontFamily = FontFamily.Monospace,
            )
        }

        AnimatedVisibility(
            visible = visibility,
            enter = slideInVertically()
        ) {
            DaySelectorRecyclerView(
                FontFamily.Monospace,
                selectedDays,
                onSelectedDay = onSelectedDay
            )
        }
    }
}

/**
 * The user can save the values entered with this button.
 * @param: onClicked -> high order function
 */
@Composable
fun FormSaveButton(onClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(0.8f),
            onClick = onClicked,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary,
                backgroundColor = MaterialTheme.colorScheme.onSecondary,
            )
        ) {
            Text("Save", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

/** Always place the preview at the bottom! */
@Composable
@Preview
fun AddPlantScreenPreview() {
//    PlantSaverApplicationTheme {
//        AddPlantScreen(rememberNavController(), null)
//    }
}
