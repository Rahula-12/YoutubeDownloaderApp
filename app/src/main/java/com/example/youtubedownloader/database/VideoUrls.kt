package com.example.youtubedownloader.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VideoUrls(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,

    @ColumnInfo(name = "VideoUrl")
    val videoUrl:String,

    @ColumnInfo(name="VideoTitle")
    val title:String,

    @ColumnInfo(name = "Date")
    val date: String,

    @ColumnInfo(name = "Time")
    val time: String
)
