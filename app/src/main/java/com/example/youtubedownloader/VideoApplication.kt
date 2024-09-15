package com.example.youtubedownloader

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VideoApplication : Application() {
    // Using by lazy so the database is only created when needed
    // rather than when the application starts

    override fun onCreate() {
        super.onCreate()
        val notificationChannel=
            NotificationChannel("success_channel","Video Download Started", NotificationManager.IMPORTANCE_HIGH)
        val notificationManager=getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

}