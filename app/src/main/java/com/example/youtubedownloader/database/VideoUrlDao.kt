package com.example.youtubedownloader.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoUrlDao {

    @Query("SELECT * FROM VideoUrls order by date desc")
    fun getUrls(): Flow<List<VideoUrls>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUrl(videoUrl:VideoUrls)

    @Query("DELETE from VideoUrls where VideoUrl=:url")
    suspend fun deleteUrl(url:String)
}