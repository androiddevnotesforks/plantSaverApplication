package com.example.plantsaverapplication.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.plantsaverapplication.MainActivity
import com.example.plantsaverapplication.R
import org.joda.time.DateTime


/**
 * Krizbai Csaba - 2023.1.2 - AlarmReceiver class
 * This class will be called by our AlarmManager at some specific time in the future and this class will fire the notification at that instant
 *
 * You can find alarm manager tutorial and tips here:
 * https://proandroiddev.com/everything-you-need-to-know-about-adding-notifications-with-alarm-manager-in-android-cb94a92b3235
 */

private const val INTENT_REQUEST_CODE = 999
private const val NOTIFICATION_ID = 123
private const val TAG = "AlarmReceiver"

class AlarmReceiver : BroadcastReceiver() {

    /** Sends notification when receives alarm and then reschedule the reminder again */
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Alarm received!")

        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager

        // Get intent extra
        val plantName = intent.getStringExtra(RemindersManager.INTENT_PLANT_NAME)
        val plantID = intent.getLongExtra(RemindersManager.INTENT_PLANT_ID, 0L)

        // Send notification to user
        notificationManager.sendReminderNotification(
            applicationContext = context,
            channelId = RemindersManager.NOTIFICATION_CHANNEL,
            plantName = plantName.toString()
        )

        // Reschedule notification
        RemindersManager.startReminder(
            context = context.applicationContext,
            reminderTime = DateTime().plusWeeks(1),
            plantName = plantName.toString(),
            plantID = plantID)
    }
}


fun NotificationManager.sendReminderNotification(
    applicationContext: Context,
    channelId: String,
    plantName: String,
) {

    //Create PendingIntent
    val pendingIntent = getActivityIntent(
        applicationContext,
        INTENT_REQUEST_CODE,
        Intent(applicationContext, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    //Build notification
    val notification =
        NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Time to work!")
            .setContentText("You need to water your flower ($plantName)")
            .setSmallIcon(R.drawable.plant_in_hand) // TODO: Add new notification icon
            .setStyle(
                NotificationCompat.BigTextStyle().bigText("You need to water your flowers ($plantName)"))
            .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent)
            .setAutoCancel(true) // Disappear notification when user taps on it
            .build()

    //Send notification
    notify(NOTIFICATION_ID, notification)
}


/**
 * I created new public function for PendingIntent creation.
 * That's how I solved problem "Missing PendingIntent mutability flag".
 * TODO: We need a better solution.
 * */
fun getActivityIntent(context: Context?, id: Int, intent: Intent?, flag: Int): PendingIntent {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_IMMUTABLE or flag)
    } else {
        PendingIntent.getActivity(context, id, intent, flag)
    }
}



