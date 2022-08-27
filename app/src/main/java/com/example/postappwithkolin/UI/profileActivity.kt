package com.example.postappwithkolin.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.postappwithkolin.Model.Post_recycler
import com.example.postappwithkolin.R
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class profileActivity : AppCompatActivity(),Post_recycler.OnItemClickListener{

    lateinit var iv_profileImage: ImageView
    lateinit var tv_userName: TextView
    lateinit var rv_posts: RecyclerView

    lateinit var rv :Post_recycler

    var mAuth = Firebase.auth

    var userName:String ?=null
    var userPhoto:String?=null

    var model:SAGDataFromDataBase = SAGDataFromDataBase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Initialize Views
        iv_profileImage = findViewById(R.id.profilePhoto)
        tv_userName = findViewById(R.id.profileUserName)
        rv_posts = findViewById(R.id.profilePosts)




        //get userName and UserPhoto from mainActivity a
        val intent = intent
        userName = intent.getStringExtra("UserName")
        userPhoto = intent.getStringExtra("UserPhoto")
        tv_userName.text = userName
        Picasso.get().load(userPhoto).into(iv_profileImage)



        model = ViewModelProvider(this).get(SAGDataFromDataBase::class.java)

        model.getPostsWitSameUserName(userName!!)

        //observer to get data that have the same UserName
        model.mutable.observe(this , Observer {
            rv = Post_recycler(it , this)
            rv_posts.adapter = rv
            rv_posts.layoutManager = LinearLayoutManager(this)
            rv_posts.setHasFixedSize(true)
        })



    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }





}