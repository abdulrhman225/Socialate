package com.example.postappwithkolin.Model

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.postappwithkolin.R
import com.squareup.picasso.Picasso

class Post_recycler(private val list: ArrayList<UserPost>, private val listener: OnItemClickListener ,
private val listener1 : onCommentButtonClick
) :
    RecyclerView.Adapter<Post_recycler.viewHolder>() {




    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tv_userName: TextView
        var PostComment: TextView
        var postImage: ImageView
        var UserPhoto: ImageView
        var Comment_post: Button
        var PostVideo :VideoView
        var fl_forVideo :FrameLayout

        init {
            tv_userName = itemView.findViewById(R.id.custom_UserName)
            PostComment = itemView.findViewById(R.id.custom_PostComment)
            postImage = itemView.findViewById(R.id.custom_PostImage)
            UserPhoto = itemView.findViewById(R.id.custom_UserPhoto)
            Comment_post = itemView.findViewById(R.id.custom_Comment)
            PostVideo = itemView.findViewById(R.id.custom_postVideo)
            fl_forVideo = itemView.findViewById(R.id.custom_fl_forVideo)

            tv_userName.setOnClickListener(this)
            Comment_post.setOnClickListener(this)



        }

        override fun onClick(p0: View?) {
            val position  = adapterPosition
            if (p0!!.id == R.id.custom_UserName)
                listener.onItemClick(position)

            else if (p0.id == R.id.custom_Comment){
                listener1.onCommentClick(position)
            }
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
        if (!userPost.postVideo.equals("null")) {
            holder.fl_forVideo.visibility = View.VISIBLE
            holder.PostVideo.setVideoURI(Uri.parse(userPost.postVideo))
            holder.PostVideo.start()



        }


        Picasso.get().load(userPost.UserPhoto).into(holder.UserPhoto)


    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)

    }

    interface onCommentButtonClick{
        fun onCommentClick(position:Int)
    }


}