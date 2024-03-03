package com.example.youtubedownloader.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoUrlDao {

    @Query("SELECT * FROM VideoUrls order by date desc")
    fun getUrls(): Flow<List<VideoUrls>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUrl(videoUrls: VideoUrls)

    @Query("delete from videourls where videourl=:url")
    fun deleteUrl(url:String)
}