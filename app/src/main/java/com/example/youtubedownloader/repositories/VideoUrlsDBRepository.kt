package com.example.youtubedownloader.repositories

import android.util.Log
import com.example.youtubedownloader.database.VideoUrlDao
import com.example.youtubedownloader.database.VideoUrls
import javax.inject.Inject

class VideoUrlsDBRepository @Inject constructor(private val videoUrlDao: VideoUrlDao) {

    fun getUrls()=videoUrlDao.getUrls()

    suspend fun insertUrl(videoUrl: VideoUrls){
        Log.d("title update inside fun","title=${videoUrl.title} time=${System.currentTimeMillis()}")
        Log.d("time inside fun",System.currentTimeMillis().toString())
        videoUrlDao.insertUrl(videoUrl)
    }

    suspend fun deleteUrl(url:String) {
         videoUrlDao.deleteUrl(url)
     }
}