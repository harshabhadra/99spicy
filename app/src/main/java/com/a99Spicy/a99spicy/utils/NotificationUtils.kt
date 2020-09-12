package com.a99Spicy.a99spicy.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.a99Spicy.a99spicy.MainActivity
import com.a99Spicy.a99spicy.R

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.createNotification(title:String = "Greetings from 99Spicy.com",message:String,applicationContext:Context){

    //Creating a pending intent
    val intent = Intent(applicationContext, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    //Get an instance of NotificationCompat.Builder
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel))
        .setSmallIcon(R.drawable.app_logo)
        .setColor(ContextCompat.getColor(applicationContext,R.color.colorPrimary))
        .setContentTitle(title)
        .setContentText(message)
        .setAutoCancel(true)
        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        .setContentIntent(pendingIntent)
        .setStyle(NotificationCompat.BigTextStyle())
        .setPriority(NotificationCompat.PRIORITY_HIGH)


    //Create the notification and notify user
    notify(NOTIFICATION_ID,builder.build())
}