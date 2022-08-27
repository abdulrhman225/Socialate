package com.example.postappwithkolin.Model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.postappwithkolin.R
import com.squareup.picasso.Picasso

class Post_recycler(private val list: ArrayList<UserPost>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<Post_recycler.viewHolder>() {


    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tv_userName: TextView
        var PostComment: TextView
        var postImage: ImageView
        var UserPhoto: ImageView

        init {
            tv_userName = itemView.findViewById(R.id.custom_UserName)
            PostComment = itemView.findViewById(R.id.custom_PostComment)
            postImage = itemView.findViewById(R.id.custom_PostImage)
            UserPhoto = itemView.findViewById(R.id.custom_UserPhoto)

            tv_userName.setOnClickListener(this)


        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            listener.onItemClick(position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_post, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        var userPost = list.get(position)

        holder.tv_userName.text = userPost.UserName
        holder.PostComment.text = userPost.postComment


        if (!userPost.postImage.equals("null")) {
            holder.postImage.visibility = View.VISIBLE
            Picasso.get().load(userPost.postImage).into(holder.postImage)
        }


        Picasso.get().load(userPost.UserPhoto).into(holder.UserPhoto)


    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)

    }


}