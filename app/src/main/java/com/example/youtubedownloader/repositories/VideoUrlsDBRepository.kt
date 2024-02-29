package com.example.youtubedownloader.repositories

import com.example.youtubedownloader.database.VideoUrlDao
import com.example.youtubedownloader.database.VideoUrls
import javax.inject.Inject

class VideoUrlsDBRepository @Inject constructor(private val videoUrlDao: VideoUrlDao) {

    fun getUrls()=videoUrlDao.getUrls()

    suspend fun insertUrl(videoUrl: VideoUrls)=videoUrlDao.insertUrl(videoUrl)

    suspend fun deleteUrl(url:String)=videoUrlDao.deleteUrl(url)
}