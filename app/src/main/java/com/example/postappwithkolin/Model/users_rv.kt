package com.example.postappwithkolin.Model

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.postappwithkolin.R
import com.squareup.picasso.Picasso

class users_rv(
    private val rv_users: ArrayList<UserInformation>,
    private val listener: OnCompleteListener
) : Adapter<users_rv.vh>() {


    inner class vh(itemView: View) : ViewHolder(itemView) , OnClickListener {
        val iv_profileImage: ImageView
        val tv_userName:TextView


        init {
            iv_profileImage = itemView.findViewById(R.id.main_IV_userImage)
            tv_userName     = itemView.findViewById(R.id.customUsers_UserName)

            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
           val position: Int = adapterPosition
            listener.onComplete(position)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vh {
        var VH =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_users_card, parent, false);
        var viewHolder: vh = vh(VH)
        return viewHolder;
    }

    override fun onBindViewHolder(holder: vh, position: Int) {
        var user:UserInformation = rv_users.get(position)

        Picasso.get().load(user.UserPhoto).into(holder.iv_profileImage)
        holder.tv_userName.text = user.UserName
    }

    override fun getItemCount(): Int {
        return rv_users.size
    }

    interface OnCompleteListener {
        fun onComplete(position: Int)
    }
}