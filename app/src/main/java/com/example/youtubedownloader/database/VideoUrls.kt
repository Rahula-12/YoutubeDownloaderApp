package com.example.youtubedownloader.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VideoUrls(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,

    @ColumnInfo(name = "VideoUrl")
    var videoUrl:String,

    @ColumnInfo(name="VideoTitle")
    var title:String,

    @ColumnInfo(name = "Date")
    var date: String,

    @ColumnInfo(name = "Time")
    var time: String
)
