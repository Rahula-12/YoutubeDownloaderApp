package com.example.youtubedownloader.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VideoUrls::class], version = 2, exportSchema = false)
abstract class VideoUrlsRoomDatabase : RoomDatabase() {

    abstract fun itemDao(): VideoUrlDao
}