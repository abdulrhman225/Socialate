package com.example.postappwithkolin.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.postappwithkolin.Model.Message_recycle
import com.example.postappwithkolin.Model.UserMessages
import com.example.postappwithkolin.R
import com.example.postappwithkolin.SourceData.MessagesSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*

class MessageActivity : AppCompatActivity() {

    lateinit var userName :TextView
    lateinit var UserPhoto:ImageView
    lateinit var MessagesList :RecyclerView
    lateinit var et_writeMessage :EditText
    lateinit var SendMessage :CircleImageView

    var UserNameAccepter :String ?= null
    var UserNameSender :String ?= null
    var userPhoto :String ?= null

    var mAuth = Firebase.auth


    //Initialize MessageSource
    var messagesSource:MessagesSource = MessagesSource()

    var rv:Message_recycle ?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        //Initialize Views
        userName = findViewById(R.id.Message_UserName)
        UserPhoto = findViewById(R.id.MessageUserPhoto)
        MessagesList = findViewById(R.id.Message_List)
        et_writeMessage = findViewById(R.id.Message_message)
        SendMessage = findViewById(R.id.Message_send)


        //get UserName and UserPhoto and put here
        val intent = intent
        UserNameAccepter = intent.getStringExtra("UserNameAccepter")
        UserNameSender = mAuth.currentUser!!.displayName
        userPhoto = intent.getStringExtra("UserPhoto")
        userName.text = UserNameAccepter
        Picasso.get().load(userPhoto).into(UserPhoto)


        //Send Message Information to FireBase
        SendMessage.setOnClickListener(View.OnClickListener {
            var message = et_writeMessage.text.toString()
            if (message.equals("")) {

            } else {
                var userMasses: UserMessages =
                    UserMessages(UserNameSender!!, UserNameAccepter!!, message)
                    messagesSource.uploadMessage(userMasses)

            }

        })

        //observe the Data that coming from firebase
        messagesSource =ViewModelProvider(this).get(MessagesSource::class.java)

        messagesSource.getMessages(UserNameSender!! , UserNameAccepter!!)


        messagesSource.massage.observe(this, Observer {
                rv = Message_recycle(it, UserNameSender!!, UserNameAccepter!!)
                    MessagesList.adapter = rv
                    MessagesList.layoutManager = LinearLayoutManager(this)
                    MessagesList.setHasFixedSize(true)


        })





    }
}