package com.example.youtubedownloader.di

import android.content.Context
import androidx.room.Room
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.youtubedownloader.database.VideoUrlDao
import com.example.youtubedownloader.database.VideoUrlsRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(components=[SingletonComponent::class],replaces = [VideoModule::class])
@Module
class TestVideoModule {

    @Singleton
    @Provides
    fun providesVideoUrlDao(roomDatabase: VideoUrlsRoomDatabase): VideoUrlDao {
        return roomDatabase.itemDao()
    }

    @Singleton
    @Provides
    fun providesVideoUrlsDB(@ApplicationContext context: Context): VideoUrlsRoomDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            VideoUrlsRoomDatabase::class.java,
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesVolleyQueue(@ApplicationContext context: Context): RequestQueue {
        return Volley.newRequestQueue(context)
    }



}