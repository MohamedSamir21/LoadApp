package com.udacity

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

    // Create the content intent for the notification, which launches (DetailActivity)
    val intent = Intent(applicationContext, DetailActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Custom Style
    val completeDownloadImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_assistant_black_24dp
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(completeDownloadImage)
        .bigLargeIcon(null)


    // Adding details action.
    val detailsIntent = Intent(applicationContext, DetailActivity::class.java)
    val detailsPendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        REQUEST_CODE,
        detailsIntent,
        FLAGS)

    // To support older versions, we get an instance of NotificationCompat.Builder
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.details_notification_channel_id)
    )
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(messageBody)
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            detailsPendingIntent
        )
        .setContentIntent(pendingIntent)
        .setStyle(bigPicStyle)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setLargeIcon(completeDownloadImage)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}