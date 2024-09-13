package com.example.youtubedownloader.viewmodel

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.*
import com.example.youtubedownloader.data.VideoItem
import com.example.youtubedownloader.database.VideoUrls
import com.example.youtubedownloader.repositories.VideoUrlsDBRepository
import com.example.youtubedownloader.repositories.VideoUrlsNetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class YoutubeDownloaderViewModel @Inject constructor(
    private val videoUrlsDBRepository: VideoUrlsDBRepository,
    private val videoUrlsNetworkRepository: VideoUrlsNetworkRepository
) : ViewModel() {
    // LiveData for observing URLs from the database
    val urls: LiveData<List<VideoUrls>> = videoUrlsDBRepository.getUrls()
    private val outputUrls: MutableList<VideoItem> = mutableListOf()

    private val _thumbnail = MutableLiveData<String>()
    val thumbnail: LiveData<String>
        get() = _thumbnail
    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    init {
        // Initializing LiveData values
        _thumbnail.value = ""
        _title.value = ""
    }

    @SuppressLint("SimpleDateFormat")
    fun insertUrl(url: String) {
        // Deleting previous URL if any and inserting the new URL into the database
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.format(Date()).toString()
        val time = Calendar.getInstance().time.toString()
        var title = ""
        viewModelScope.launch(Dispatchers.IO) {
                title = videoUrlsNetworkRepository.insertUrlRequestIntoQueue(
                    getYoutubeID(url)
                )
            withContext(Dispatchers.Main) {
                deleteUrl(url)
            }
            videoUrlsDBRepository.insertUrl(
                VideoUrls(
                    videoUrl = url,
                    title = title,
                    date = date,
                    time = time
                )
            )
        }
    }

    fun deleteUrl(deleteUrl:String) {
        // Deleting URL from the database
        Log.d("deleteOut","$deleteUrl ${System.currentTimeMillis()}")
        viewModelScope.launch(Dispatchers.IO){
            Log.d("delete",deleteUrl)
            videoUrlsDBRepository.deleteUrl(deleteUrl)
        }
        resetAll()
    }
//1709745122537
    fun assignUrls(url: String) {
        // Assigning download URL and delete URL and fetching thumbnail and title
//        deleteUrl = url
//        Log.d("assign","$deleteUrl $downloadUrl ${System.currentTimeMillis()}")
        thumbUrlAndTitle(url)
    }

    fun getVideo():VideoItem {
        return outputUrls[0]
    }

    fun resetAll() {
        // Resetting all values to default
        outputUrls.clear()
        _thumbnail.value = ""
        _title.value = ""
    }

    fun isYoutubeUrl(youTubeURl: String): Boolean {
        // Checking if URL is a valid YouTube URL
        val pattern = Pattern.compile("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")
        val matcher = pattern.matcher(youTubeURl)
        return matcher.matches()
    }

    private fun thumbUrlAndTitle(url:String) {
        // Fetching thumbnail and title from API
        videoUrlsNetworkRepository.insertThumbUrlAndTitleRequestIntoQueue(getYoutubeID(url), updateThumbAndTitle = {
            s1,s2->
            _thumbnail.value=s1
            _title.value=s2
        })
        viewModelScope.launch {
            val list=videoUrlsNetworkRepository.outputVideosRequestIntoQueue(getYoutubeID(url))
//            Log.d("List",list.size.toString())
            outputUrls.addAll(list)
        }
    }

    private fun getYoutubeID(youtubeUrl: String): String? {
        // Extracting YouTube video ID from the URL
        if (TextUtils.isEmpty(youtubeUrl)) {
            return ""
        }
        var  video_id: String? = ""
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
