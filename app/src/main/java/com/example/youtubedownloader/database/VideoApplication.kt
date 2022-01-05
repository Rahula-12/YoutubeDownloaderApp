package com.example.youtubedownloader.database

import android.app.Application

class VideoApplication : Application() {
    // Using by lazy so the database is only created when needed
    // rather than when the application starts
    val database: VideoUrlsRoomDatabase by lazy { VideoUrlsRoomDatabase.getDatabase(this) }
}