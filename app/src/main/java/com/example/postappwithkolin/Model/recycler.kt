package com.example.postappwithkolin.Model

import android.graphics.Bitmap
import android.icu.number.NumberFormatter.with
import android.net.Uri
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.example.postappwithkolin.R
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import com.squareup.picasso.Picasso

class recycler(var list:ArrayList<UserPost>) : RecyclerView.Adapter<recycler.viewHolder>() {


    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
         var tv_userName:TextView
         var PostComment:TextView
         var postImage:ImageView
         var UserPhoto:ImageView
        init {
            tv_userName = itemView.findViewById(R.id.custom_UserName)
            PostComment = itemView.findViewById(R.id.custom_PostComment)
            postImage = itemView.findViewById(R.id.custom_PostImage)
            UserPhoto = itemView.findViewById(R.id.custom_UserPhoto)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view:View = LayoutInflater.from(parent.context).inflate(R.layout.custom_post , parent , false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        var userPost = list.get(position)
        holder.tv_userName.text = userPost.UserName
        holder.PostComment.text = userPost.postComment
        Picasso.get().load(userPost.postImage).into(holder.postImage)
        Picasso.get().load(userPost.UserPhoto).into(holder.UserPhoto)


    }

    override fun getItemCount(): Int {
        return list.size
    }


}