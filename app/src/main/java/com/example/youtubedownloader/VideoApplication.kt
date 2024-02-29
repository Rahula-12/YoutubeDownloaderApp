package com.example.youtubedownloader.database

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VideoApplication : Application() {
    // Using by lazy so the database is only created when needed
    // rather than when the application starts
}