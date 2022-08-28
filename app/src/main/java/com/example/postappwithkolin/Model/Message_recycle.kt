package com.example.postappwithkolin.Model

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.example.postappwithkolin.R
import org.w3c.dom.Text

class Message_recycle(
    var Messages: ArrayList<UserMessages>,
    var UserNameSender: String,
    var UserNameAccepter: String
) : RecyclerView.Adapter<Message_recycle.viewHolder>() {

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var SenderUser: TextView
        var Message: TextView
        var message: LinearLayout

        init {
//            SenderUser = itemView.findViewById(R.id.UserName_Sender)
            Message = itemView.findViewById(R.id.UserName_message)
            message = itemView.findViewById(R.id.Message)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_messages, parent, false)
        return viewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        var userMessges: UserMessages = Messages.get(position)


//        holder.SenderUser.text = userMessges.SenderUser
        holder.Message.text = userMessges.Message


        if (userMessges.SenderUser.equals(UserNameSender)) {
            holder.message.gravity = Gravity.LEFT


        }

        else if (!userMessges.SenderUser.equals(UserNameSender)) {
            holder.message.gravity = Gravity.RIGHT



        }


    }

    override fun getItemCount(): Int {
        return Messages.size
    }
}