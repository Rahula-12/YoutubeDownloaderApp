package com.example.youtubedownloader.di

import android.content.Context
import androidx.room.Room
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.youtubedownloader.database.VideoUrlDao
import com.example.youtubedownloader.database.VideoUrlsRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object VideoModule {

    @Singleton
    @Provides
    fun providesVideoUrlDao(roomDatabase: VideoUrlsRoomDatabase):VideoUrlDao{
        return roomDatabase.itemDao()
    }

    @Singleton
    @Provides
    fun providesVideoUrlsDB(@ApplicationContext context: Context):VideoUrlsRoomDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            VideoUrlsRoomDatabase::class.java,
            "videoUrls_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Singleton
    @Provides
    fun providesVollyQueue(@ApplicationContext context: Context):RequestQueue {
        return Volley.newRequestQueue(context)
    }

}