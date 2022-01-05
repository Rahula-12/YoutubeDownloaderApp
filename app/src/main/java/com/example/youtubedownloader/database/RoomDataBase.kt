package com.example.youtubedownloader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VideoUrls::class], version = 2, exportSchema = false)
abstract class VideoUrlsRoomDatabase : RoomDatabase() {

    abstract fun itemDao(): VideoUrlDao

    companion object {
        @Volatile
        private var INSTANCE: VideoUrlsRoomDatabase? = null

        fun getDatabase(context: Context): VideoUrlsRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VideoUrlsRoomDatabase::class.java,
                    "videoUrls_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}