package com.example.youtubedownloader.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoUrlDao {

    @Query("SELECT * FROM VideoUrls order by date desc")
    fun getUrls(): LiveData<List<VideoUrls>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     suspend fun insertUrl(videoUrls: VideoUrls)

    @Query("delete from videourls where VideoUrl=:videoUrl")
    suspend fun deleteUrl(videoUrl:String)
}

// https://youtu.be/aWWJWfivRp0?si=qe8kWF4h9tXE6l0C
// https://youtu.be/q1wK0r51ARs?si=nFESvRCXLWrVXgi7