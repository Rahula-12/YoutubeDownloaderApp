package com.example.youtubedownloader.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubedownloader.R
import com.example.youtubedownloader.database.VideoUrls
import com.example.youtubedownloader.fragments.ItemClicked

class VideoUrlAdapter(private val itemClicked: ItemClicked):RecyclerView.Adapter<VideoUrlAdapter.VideoUrlViewHolder>(){
    private val urls:MutableList<VideoUrls> = mutableListOf()
    class VideoUrlViewHolder(view: View):RecyclerView.ViewHolder(view){
        val videoUrl: TextView =view.findViewById(R.id.videoUrl)
        val time: TextView =view.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoUrlViewHolder {
        val layout=LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        val videoUrlViewHolder=VideoUrlViewHolder(layout)
        layout.setOnClickListener{
            itemClicked.onItemClicked(urls[videoUrlViewHolder.adapterPosition].videoUrl)
        }
        return videoUrlViewHolder
    }

    override fun onBindViewHolder(holder: VideoUrlViewHolder, position: Int) {
        holder.videoUrl.text=urls[position].title
        holder.time.text=urls[position].time
    }

    override fun getItemCount(): Int {
        return urls.size
    }
    fun updateList(newList:List<VideoUrls>){
        urls.clear()
        urls.addAll(newList)
        notifyDataSetChanged()
    }
}
