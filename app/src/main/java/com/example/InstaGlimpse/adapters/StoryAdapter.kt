package com.example.InstaGlimpse.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.InstaGlimpse.R
import com.example.InstaGlimpse.util.DirectoryUtils
import com.example.InstaGlimpse.util.Utils.startDownload
import com.squareup.picasso.Picasso
import papayacoders.instastory.models.ItemModel

class StoryAdapter : ListAdapter<ItemModel, StoryAdapter.StoryViewHolder>(DiffUtil()){

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<ItemModel>() {
        override fun areItemsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class StoryViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val imageView = view.findViewById<ImageView>(R.id.story_image)
        val download = view.findViewById<ImageView>(R.id.download_story)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = StoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.story_item_layout, parent, false))

        return view
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val item = getItem(position)
        Picasso.get().load(item.imageversions2.candidates[0].url).into(holder.imageView)

        holder.download.setOnClickListener{
            if(item.mediatype == 2){
                val url = item.videoversions[0].url
                Log.d("ISHANK", url)
                startDownload(url, DirectoryUtils.DIRECTORY,holder.itemView.context,
                    "Instagram_story_"+System.currentTimeMillis()+".mp4")
            }else{
                val url2 = item.imageversions2.candidates[0].url
                startDownload(url2, DirectoryUtils.DIRECTORY,holder.itemView.context,
                    "Instagram_story_"+System.currentTimeMillis()+".png")
            }
        }
    }

}