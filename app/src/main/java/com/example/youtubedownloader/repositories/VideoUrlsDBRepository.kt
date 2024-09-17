package com.example.youtubedownloader.repositories

import android.util.Log
import com.example.youtubedownloader.database.VideoUrlDao
import com.example.youtubedownloader.database.VideoUrls
import javax.inject.Inject

class VideoUrlsDBRepository @Inject constructor(private val videoUrlDao: VideoUrlDao) {

    fun getUrls()=videoUrlDao.getUrls()

    suspend fun insertUrl(videoUrl: VideoUrls){
//        Log.d("title update inside fun","title=${videoUrl.title} time=${System.currentTimeMillis()}")
//        Log.d("testing_insert",System.currentTimeMillis().toString())
        videoUrlDao.insertUrl(videoUrl)
    }

    suspend fun deleteUrl(deleteUrl:String) {
         videoUrlDao.deleteUrl(deleteUrl)
     }

    suspend fun getLatestUrl()=videoUrlDao.getLatestUrl()

    suspend fun updateLatestUrl(videoUrl: VideoUrls)=videoUrlDao.updateLatestUrl(videoUrl)

    suspend fun deleteUrlById(id:Int)=videoUrlDao.deleteUrlById(id)
}