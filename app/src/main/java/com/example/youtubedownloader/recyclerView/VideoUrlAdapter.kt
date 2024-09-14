package com.example.youtubedownloader.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubedownloader.R
import com.example.youtubedownloader.database.VideoUrls

class VideoUrlAdapter(private val onItemClicked:(String)->Unit):ListAdapter<VideoUrls,VideoUrlAdapter.VideoUrlViewHolder>(VideoDiffCallback()){
    //private val urls:MutableList<VideoUrls> = mutableListOf()
    class VideoUrlViewHolder(view: View):RecyclerView.ViewHolder(view){
        val videoUrl: TextView =view.findViewById(R.id.videoUrl)
        val time: TextView =view.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoUrlViewHolder {
        val layout=LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        val videoUrlViewHolder=VideoUrlViewHolder(layout)
        layout.setOnClickListener{
            onItemClicked(getItem(0).videoUrl)
        }
        return videoUrlViewHolder
    }

    override fun onBindViewHolder(holder: VideoUrlViewHolder, position: Int) {
        holder.videoUrl.text=getItem(0).title
        holder.time.text=getItem(0).time
    }
}

class VideoDiffCallback():DiffUtil.ItemCallback<VideoUrls>(){

    override fun areItemsTheSame(oldItem: VideoUrls, newItem: VideoUrls): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: VideoUrls, newItem: VideoUrls): Boolean {
        return oldItem==newItem
    }

}
