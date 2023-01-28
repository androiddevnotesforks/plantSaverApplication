package com.example.plantsaverapplication.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import org.joda.time.DateTime

/**
 * Krizbai Csaba - 2023.1.2 - RemindersManager class
 * With this class you can create and stop alarms with AlarmManager
 *
 * You can find alarm manager tutorial and tips here:
 * https://proandroiddev.com/everything-you-need-to-know-about-adding-notifications-with-alarm-manager-in-android-cb94a92b3235
 */

object RemindersManager {

    // Reminder notification request code
    private const val DEF_NOTIFICATION_REQUEST = 222
    const val NOTIFICATION_CHANNEL = "WATER_TIME"
    const val INTENT_PLANT_NAME = "plant_name"
    const val INTENT_PLANT_ID = "plant_ID"
    const val TAG = "ReminderManager"

    /** Initialize notification channel */
    fun createNotificationsChannels(context: Context) {
        val channel =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(NOTIFICATION_CHANNEL, "TEXT", NotificationManager.IMPORTANCE_HIGH)
            } else {
                TODO("VERSION.SDK_INT < O")
            }

        ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        )?.createNotificationChannel(channel)

    }

    /** Start reminder */
    fun startReminder(
        context: Context,
        reminderTime: DateTime,
        plantName: String,
        plantID: Long
    ) {
        Log.d(TAG, "User created reminder at: $reminderTime with ID:$plantID!")

        // Create service
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create PendingIntent
        val pendingIntent = getBroadcastIntent(
            context,
            plantID.toInt(),
            Intent(context, AlarmReceiver::class.java)
                .putExtra(INTENT_PLANT_NAME, plantName)
                .putExtra(INTENT_PLANT_ID, plantID),
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        // If the trigger time you specify is in the past, the alarm triggers immediately.
        if(reminderTime.isBefore(DateTime().millis)){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime.plusWeeks(1).millis, pendingIntent)
            return
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime.millis, pendingIntent)
    }

    /** Stop reminder */
    fun stopReminder(context: Context, reminderId: Int = DEF_NOTIFICATION_REQUEST) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getBroadcastIntent(
            context,
            reminderId,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

}


/**
 * I created new public function for PendingIntent creation.
 * That's how I solved problem "Missing PendingIntent mutability flag".
 * TODO: We need a better solution.
 * */

fun getBroadcastIntent(context: Context?, id: Int, intent: Intent, flag: Int): PendingIntent {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE or flag)
    } else {
        PendingIntent.getBroadcast(context, id, intent, flag)
    }
}


