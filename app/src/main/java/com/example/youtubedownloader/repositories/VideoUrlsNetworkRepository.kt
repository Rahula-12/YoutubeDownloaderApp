package com.example.youtubedownloader.repositories

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.example.youtubedownloader.data.VideoItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class VideoUrlsNetworkRepository @Inject constructor(private val queue: RequestQueue,@ApplicationContext private val context: Context) {

    fun insertUrlRequestIntoQueue(youtubeId:String?):String {
        val currUrl = "https://ytstream-download-youtube-videos.p.rapidapi.com/dl?id=${youtubeId}&rapidapi-key=1e59c5254cmsh65f87366aa4844fp138d18jsn4df36025d134"
        var title=""
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, currUrl, null, { response ->
                 title = response.getString("title")
            },
            { error ->
                Log.d("insertion error", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
        return title
    }

    fun insertThumbUrlAndTitleRequestIntoQueue(youtubeId:String?): List<String>{
        val currUrl =
            "https://ytstream-download-youtube-videos.p.rapidapi.com/dl?id=${youtubeId}&rapidapi-key=1e59c5254cmsh65f87366aa4844fp138d18jsn4df36025d134"
        val thumbAndTitle= mutableListOf<String>()
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, currUrl, null,
            { response ->
                thumbAndTitle.add(response.getString("thumb"))
                thumbAndTitle.add(response.getString("title"))
            },
            { error ->
                Log.d("problem", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
        return thumbAndTitle
    }

    fun outputVideosRequestIntoQueue(
        pos:Int,
        youtubeId: String?,
        updateDownloadId:(Long)->Unit={},
        updateDownloadLiveData:(Long?)->Unit={}
    ):List<VideoItem> {
        val outputUrls:MutableList<VideoItem> = mutableListOf()
        val currUrl =
            "https://ytstream-download-youtube-videos.p.rapidapi.com/dl?id=${youtubeId}&rapidapi-key=1e59c5254cmsh65f87366aa4844fp138d18jsn4df36025d134"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, currUrl, null,
            { response ->
                val obj = response.getJSONObject("link")
                outputUrls.add(VideoItem(obj.getJSONArray("18").getString(0)))
                outputUrls.add(VideoItem(obj.getJSONArray("22").getString(0)))
                val DIRECTORY = "/YoutubeVideos/"
                val DIRECTORY_FOLDER =
                    File("${Environment.getExternalStorageDirectory()}/Download/${DIRECTORY}")
                if (!DIRECTORY_FOLDER.exists()) {
                    DIRECTORY_FOLDER.mkdirs()
                }
                val request = DownloadManager.Request(Uri.parse(outputUrls[pos].url))
                    .setTitle("YoutubeVideo_" + System.currentTimeMillis().toString() + ".mp4")
                    .setDescription("Downloading Video")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setAllowedOverMetered(true)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        DIRECTORY + "YoutubeVideo_" + System.currentTimeMillis().toString() + ".mp4"
                    )
                val dm: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                updateDownloadId(dm.enqueue(request))
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(
                        File(
                            Environment.DIRECTORY_DOWNLOADS + "/" + DIRECTORY + "YoutubeVideo_" + System.currentTimeMillis()
                                .toString() + ".mp4"
                        ).absolutePath
                    ),
                    null
                )
                { _, _ ->
                }
                val br = object : BroadcastReceiver() {
                    override fun onReceive(p0: Context?, p1: Intent?) {
                        val id = p1?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        updateDownloadLiveData(id)
                    }
                }
                context.registerReceiver(br, (IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)))
            },
            { error ->
                Log.d("problem", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
        return outputUrls
    }

}