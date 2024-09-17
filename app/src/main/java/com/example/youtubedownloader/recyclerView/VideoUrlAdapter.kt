package com.example.youtubedownloader.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.youtubedownloader.R
import com.example.youtubedownloader.database.VideoUrls

class VideoUrlAdapter(
    private val onItemClicked:(String)->Unit,
    private val onItemLongClicked:(Int)->Unit,
    var urlInserted:Boolean
):ListAdapter<VideoUrls,VideoUrlAdapter.VideoUrlViewHolder>(VideoDiffCallback(),){
    //private val urls:MutableList<VideoUrls> = mutableListOf()
    class VideoUrlViewHolder(view: View):RecyclerView.ViewHolder(view){
        val videoUrl: TextView =view.findViewById(R.id.videoUrl)
        val time: TextView =view.findViewById(R.id.time)
        val divider:View=view.findViewById(R.id.divider)
        val progressBar: ProgressBar =view.findViewById<ProgressBar>(R.id.progress_circular)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoUrlViewHolder {
        val layout=LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        val videoUrlViewHolder=VideoUrlViewHolder(layout)
        return videoUrlViewHolder
    }

    override fun onBindViewHolder(holder: VideoUrlViewHolder, position: Int) {
        if(position==0 && getItem(position).title=="") {
            if(urlInserted) {
                holder.videoUrl.isVisible=true
                holder.divider.isVisible=true
                holder.time.isVisible=true
                holder.progressBar.isVisible=false
            }
            if(!urlInserted) {
                holder.videoUrl.isVisible=false
                holder.divider.isVisible=false
                holder.time.isVisible=false
                holder.progressBar.isVisible=true
            }
        }
        holder.videoUrl.text = getItem(position).title
        holder.time.text = getItem(position).time
        holder.itemView.setOnClickListener {
            try {
                onItemClicked(getItem(position).videoUrl)
            } catch (_: Exception) {

            }
        }
        holder.itemView.setOnLongClickListener{
            AlertDialog.Builder(holder.itemView.context)
                .setMessage("Do you want to delete this Url?")
                .setPositiveButton("Ok"){_,_->
                    onItemLongClicked(getItem(position).id)
                }
                .setNeutralButton("Cancel"){_,_->

                }.show()
            true
        }
    }
}

class VideoDiffCallback:DiffUtil.ItemCallback<VideoUrls>(){

    override fun areItemsTheSame(oldItem: VideoUrls, newItem: VideoUrls): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: VideoUrls, newItem: VideoUrls): Boolean {
        return oldItem.title==newItem.title
    }

}
