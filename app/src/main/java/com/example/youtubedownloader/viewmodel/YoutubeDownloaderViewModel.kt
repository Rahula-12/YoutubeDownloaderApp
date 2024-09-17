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

    private val _urlInserted:MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    val urlInserted:LiveData<Boolean> = _urlInserted

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
        _urlInserted.value=false
        // Deleting previous URL if any and inserting the new URL into the database
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.format(Date()).toString()
        val time = Calendar.getInstance().time.toString()
        var title = ""
        viewModelScope.launch(Dispatchers.IO) {
            videoUrlsDBRepository.insertUrl(
                VideoUrls(
                    videoUrl = "",
                    title = "",
                    date = date,
                    time = time
                )
            )
            title = videoUrlsNetworkRepository.insertUrlRequestIntoQueue(
                    getYoutubeID(url)
                )
            withContext(Dispatchers.Main) {
//                _urlInserted.value=true
//                _urlInserted.value=false
                deleteUrl(url)
            }
            val latestVideoUrl=videoUrlsDBRepository.getLatestUrl()
            latestVideoUrl.videoUrl=url
            latestVideoUrl.title=title
            videoUrlsDBRepository.updateLatestUrl(latestVideoUrl)
//            videoUrlsDBRepository.insertUrl(
//                VideoUrls(
//                    videoUrl = url,
//                    title = title,
//                    date = date,
//                    time = time
//                )
//            )
            withContext(Dispatchers.Main) {
                _urlInserted.value=true
            }
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

     fun deleteUrlById(id:Int) {
         viewModelScope.launch {
             videoUrlsDBRepository.deleteUrlById(id)
         }
     }

    private fun getYoutubeID(youtubeUrl: String): String {
        // Extracting YouTube video ID from the URL

        val patterns = arrayOf("^https?://(?:www\\.)?youtube\\.com/watch\\?v=([^&]+)",
            "^https?://(?:www\\.)?youtube\\.com/embed/([^/?]+)",
            "^https?://(?:www\\.)?youtu\\.be/([^/?]+)"
        )

        for (pattern in patterns) {
            val matcher = Regex(pattern).find(youtubeUrl)
            if (matcher != null) {
                return matcher.groupValues[1]
            }
        }
        return ""
    }
}
