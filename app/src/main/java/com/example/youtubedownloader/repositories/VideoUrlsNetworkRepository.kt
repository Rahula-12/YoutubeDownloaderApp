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
import com.android.volley.Header
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.example.youtubedownloader.BuildConfig
import com.example.youtubedownloader.data.VideoItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VideoUrlsNetworkRepository @Inject constructor(private val queue: RequestQueue) {
    lateinit var outputUrls:MutableList<VideoItem>
    suspend fun insertUrlRequestIntoQueue(youtubeId:String?)= suspendCoroutine<String> {
        Log.d("youtubeId",youtubeId.toString())
        val currUrl = "https://ytstream-download-youtube-videos.p.rapidapi.com/dl?id=${youtubeId}"
        val jsonObjectRequest = object :JsonObjectRequest(
            Method.GET, currUrl, null, { response ->
               // Log.d("time inside NetworkRepo",System.currentTimeMillis().toString())
                Log.d("Response",response.toString())
                try {
                    it.resume(response.getString("title"))
                }
                catch (e:Exception) {
                    it.resume(e.toString())
                }
            },
            { error ->
                it.resume(error.toString())
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers:MutableMap<String,String> = mutableMapOf(
                    "x-rapidapi-key" to BuildConfig.API_KEY,
                    "x-rapidapi-host" to "ytstream-download-youtube-videos.p.rapidapi.com"
                )
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    fun insertThumbUrlAndTitleRequestIntoQueue(youtubeId:String?,updateThumbAndTitle:(String,String)->Unit={
        _,_->
    }){
        Log.d("youtubeId",youtubeId.toString())
        outputUrls= mutableListOf()
        val currUrl =
            "https://ytstream-download-youtube-videos.p.rapidapi.com/dl?id=${youtubeId}"
        mutableListOf<String>()
        val jsonObjectRequest = object :JsonObjectRequest(
            Method.GET, currUrl, null,
            { response ->
                try {
                    outputUrls.add(VideoItem(response.getJSONArray("formats").getJSONObject(0).getString("url")))
                }
                catch (_:Exception) {

                }
                try{
                    updateThumbAndTitle(
                        response.getJSONArray("thumbnail").getJSONObject(0).getString("url"),
                        response.getString("title")
                    )
                }
                catch (_:Exception) {

                }
            },
            { error ->
                Log.d("problem", error.toString())
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers:MutableMap<String,String> = mutableMapOf(
                    "x-rapidapi-key" to BuildConfig.API_KEY,
                    "x-rapidapi-host" to "ytstream-download-youtube-videos.p.rapidapi.com"
                )
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    suspend fun outputVideosRequestIntoQueue(
        youtubeId: String?
    )= suspendCoroutine<List<VideoItem>> {
        if(::outputUrls.isInitialized)  outputUrls
        val currUrl =
            "https://ytstream-download-youtube-videos.p.rapidapi.com/dl?id=${youtubeId}"
        Log.d("youtubeId",youtubeId.toString())
        val jsonObjectRequest = object :JsonObjectRequest(
            Method.GET, currUrl, null,
            { response ->
//                val obj = response.getJSONObject("link")
                try {
                    outputUrls.add(VideoItem(response.getJSONArray("formats").getJSONObject(0).getString("url")))
//                outputUrls.add(VideoItem("https://rr3---sn-qxaelnes.googlevideo.com/videoplayback?expire=1726091938&ei=Qr7hZsOaH6_Rp-oPysa9yAw&ip=196.44.123.96&id=o-AAAMcSXUfay0fOThPrJHD-Vm_MKnb8w69zEXosLCACf6&itag=18&source=youtube&requiressl=yes&xpc=EgVo2aDSNQ%3D%3D&bui=AQmm2eyatLDOP83_DG49UQS57PWrbXtDSWI4mrh6MIRfkteUAH03CKSMmx2H52KbVbfwEZfQzwSqql4T&spc=Mv1m9nmms_XrEJr8EyYpheLFzlbag8hkiS0z1y5hgWesECCWp-a2&vprv=1&svpuc=1&mime=video%2Fmp4&ns=_xRIRwAT5MLpWmnlCgCFJyAQ&rqh=1&cnr=14&ratebypass=yes&dur=180.024&lmt=1714804883028793&c=WEB&sefc=1&txp=4538434&n=O1GvNkpT5ga4Gg&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cxpc%2Cbui%2Cspc%2Cvprv%2Csvpuc%2Cmime%2Cns%2Crqh%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AJfQdSswRgIhAIeegcyCzhJ07jTTGChaE_6GTAaIPSH2YcLOvkATuqo4AiEArHYuZCN12YDS8qxLR355zn54MH-ukKSb_QG3LYPF2BQ%3D&redirect_counter=1&rm=sn-4g5ek67l&rrc=104&fexp=24350517,24350556,24350561&req_id=1fabe2fd383ca3ee&cms_redirect=yes&ipbypass=yes&mh=S8&mip=2401:4900:8394:8cf2:14ef:f8f9:20f3:9da9&mm=31&mn=sn-qxaelnes&ms=au&mt=1726075770&mv=m&mvi=3&pl=44&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=ABPmVW0wRgIhAKBAX-S_SjOherB-zADGs7QfZp71L_EHmcgKY68DYrKXAiEA_dWRi7dMGvquF-4TFW8e2hSo3AZT5TsSh7fv8G93rmo%3D"))
                    it.resume(outputUrls)
                }
                catch (_:Exception) {

                }
            },
            { error ->
                Log.d("problem", error.toString())
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers:MutableMap<String,String> = mutableMapOf(
                    "x-rapidapi-key" to BuildConfig.API_KEY,
                    "x-rapidapi-host" to "ytstream-download-youtube-videos.p.rapidapi.com"
                )
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

}