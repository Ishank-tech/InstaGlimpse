package com.example.InstaGlimpse.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.InstaGlimpse.R
import com.example.InstaGlimpse.util.UserInterface
import com.squareup.picasso.Picasso
import papayacoders.instastory.models.TrayModel

class ProfileAdapter(private val userInterface: UserInterface) : ListAdapter<TrayModel, ProfileAdapter.ProfileViewHolder>(
    DiffUtil()
){

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<TrayModel>() {
        override fun areItemsTheSame(oldItem: TrayModel, newItem: TrayModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TrayModel, newItem: TrayModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ProfileViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val imageView = view.findViewById<ImageView>(R.id.userImage)
        val textView = view.findViewById<TextView>(R.id.userName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.profile_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val item = getItem(position)
        holder.textView.text = item.user.fullname
        Picasso.get().load(item.user.profilepicurl).into(holder.imageView)
        holder.itemView.setOnClickListener{
            userInterface.userInterfaceClick(position, item)
        }
    }

}