package com.udacity.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R


const val NOTIFICATION_ID = 0
const val FILENAME_EXTRA = "filename_extra"
const val STATUS_EXTRA = "status_extra"


/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
// May need to pass notification Id as a arg here.
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, status: String) {
    // Create the content intent to open the appropriate activity.
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(FILENAME_EXTRA, messageBody)
    contentIntent.putExtra(STATUS_EXTRA, status)

    // Pending Intent is used to open the app.
    val contentPendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    // The first thing we do is to create the intent for the notification which launches the main activity.
    val notificationBuilder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.githubRepo_notification_channel_id))

            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle("Download Complete - $status" )
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_assistant_black_24dp, applicationContext.getString(R.string.check_status_action), contentPendingIntent)

    //Deliver the notification
    notify(NOTIFICATION_ID, notificationBuilder.build())
}

