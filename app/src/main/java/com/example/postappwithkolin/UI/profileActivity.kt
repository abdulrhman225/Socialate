package com.example.postappwithkolin.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.postappwithkolin.Model.recycler
import com.example.postappwithkolin.R
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase
import com.squareup.picasso.Picasso
import java.util.zip.Inflater

class profileActivity : AppCompatActivity(),recycler.OnItemClickListener{

    lateinit var iv_profileImage: ImageView
    lateinit var tv_userName: TextView
    lateinit var rv_posts: RecyclerView

    lateinit var rv :recycler

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


        val intent = intent
        userName = intent.getStringExtra("UserName")
        userPhoto = intent.getStringExtra("UserPhoto")
        tv_userName.text = userName
        Picasso.get().load(userPhoto).into(iv_profileImage)



        model = ViewModelProvider(this).get(SAGDataFromDataBase::class.java)

        model.getPostsWitSameUserName(userName!!)

        model.mutable.observe(this , Observer {
            rv = recycler(it , this)
            rv_posts.adapter = rv
            rv_posts.layoutManager = LinearLayoutManager(this)
            rv_posts.setHasFixedSize(true)
        })
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }


}