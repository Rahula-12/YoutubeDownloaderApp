package com.example.youtubedownloader.viewmodel

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import androidx.lifecycle.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.youtubedownloader.data.VideoItem
import com.example.youtubedownloader.database.VideoUrlDao
import com.example.youtubedownloader.database.VideoUrls
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import android.text.TextUtils

class YoutubeDownloaderViewModel(private val dao:VideoUrlDao,
                                 private val context: Context):ViewModel()
{
    val urls:LiveData<List<VideoUrls>> = dao.getUrls().asLiveData()
    private var downloadUrl:String=""
    private var deleteUrl:String=""
    private val outputUrls:MutableList<VideoItem> = mutableListOf()
    var downloadId:Long=0
    private val _downloadCompleted=MutableLiveData<Boolean>()
    val downloadCompleted:LiveData<Boolean>
    get() = _downloadCompleted
    private val _thumbnail=MutableLiveData<String>()
    val thumbnail:LiveData<String>
    get() = _thumbnail
    private val _title=MutableLiveData<String>()
    val title:LiveData<String>
    get() = _title
    init {
        _downloadCompleted.value=false
        _thumbnail.value=""
        _title.value=""
    }
        @SuppressLint("SimpleDateFormat")
        fun insertUrl(url:String)
        {
            val temp=deleteUrl
            deleteUrl=url
            deleteUrl()
            deleteUrl=temp
          val sdf=SimpleDateFormat("dd/MM/yyyy")
                val date=sdf.format(Date()).toString()
                val time=Calendar.getInstance().time.toString()
            val queue=Volley.newRequestQueue(context)
            val currUrl= "https://ytstream-download-youtube-videos.p.rapidapi.com/dl?id=${getYoutubeID(url)}&rapidapi-key=1e59c5254cmsh65f87366aa4844fp138d18jsn4df36025d134"
            val jsonObjectRequest=JsonObjectRequest(
                Request.Method.GET,currUrl,null,{response->
                    val title=response.getString("title")
                    viewModelScope.launch {
                        dao.insertUrl(VideoUrls(videoUrl = url, title = title, date = date, time = time))
                    }
                },
                {
                    error->
                    Log.d("insertion error",error.toString())
                }
            )
            queue.add(jsonObjectRequest)
        }
         fun deleteUrl()
        {
           viewModelScope.launch {
                   dao.deleteUrl(deleteUrl)
           }
            resetAll()
        }
        fun assignUrls(url:String){
            downloadUrl=url
            deleteUrl=url
            thumbUrlAndTitle()
        }
        fun outPutVideos(pos:Int){
            val queue=Volley.newRequestQueue(context)
            val currUrl= "https://ytstream-download-youtube-videos.p.rapidapi.com/dl?id=${getYoutubeID(downloadUrl)}&rapidapi-key=1e59c5254cmsh65f87366aa4844fp138d18jsn4df36025d134"
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, currUrl, null,
                { response ->
                    val obj=response.getJSONObject("link")
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
                                DIRECTORY_DOWNLOADS,
                                DIRECTORY + "YoutubeVideo_" + System.currentTimeMillis().toString() + ".mp4"
                            )
                    val dm: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        downloadId = dm.enqueue(request)
                        MediaScannerConnection.scanFile(
                            context,
                            arrayOf(
                                File(
                                    DIRECTORY_DOWNLOADS + "/" + DIRECTORY + "YoutubeVideo_" + System.currentTimeMillis()
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
                                if (id == downloadId) {
                                    _downloadCompleted.value = true
                                }
                            }
                        }
                        context.registerReceiver(br, (IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)))
                },
                { error ->
                    Log.d("problem",error.toString())
                }
            )
            queue.add(jsonObjectRequest)
        }
     fun resetAll(){
        _downloadCompleted.value=false
        outputUrls.clear()
        _thumbnail.value=""
        _title.value=""
    }
    fun isYoutubeUrl(youTubeURl: String): Boolean {
        val pattern = Pattern.compile("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")
        val matcher=pattern.matcher(youTubeURl)
        return matcher.matches()
    }
    private fun thumbUrlAndTitle(){
        val queue=Volley.newRequestQueue(context)
        val currUrl= "https://ytstream-download-youtube-videos.p.rapidapi.com/dl?id=${getYoutubeID(downloadUrl)}&rapidapi-key=1e59c5254cmsh65f87366aa4844fp138d18jsn4df36025d134"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, currUrl, null,
            { response ->
                _thumbnail.value=response.getString("thumb")
                _title.value=response.getString("title")
            },
            { error ->
                Log.d("problem",error.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }
    private fun getYoutubeID(youtubeUrl: String): String? {
        if (TextUtils.isEmpty(youtubeUrl)) {
            return ""
        }
        var video_id: String? = ""
        val expression =
            "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*" // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
        val input: CharSequence = youtubeUrl
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(input)
        if (matcher.matches()) {
            val groupIndex1 = matcher.group(7)
            if (groupIndex1 != null && groupIndex1.length == 11) video_id = groupIndex1
        }
        if (TextUtils.isEmpty(video_id)) {
            if (youtubeUrl.contains("youtu.be/")) {
                val spl = youtubeUrl.split("youtu.be/".toRegex()).toTypedArray()[1]
                video_id = if (spl.contains("\\?")) {
                    spl.split("\\?".toRegex()).toTypedArray()[0]
                } else {
                    spl
                }
            }
        }
        return video_id
    }
}
class YoutubeDownloaderViewModelFactory(private val dao: VideoUrlDao,private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(YoutubeDownloaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return YoutubeDownloaderViewModel(dao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}