package com.example.postappwithkolin.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.postappwithkolin.Model.Post_recycler
import com.example.postappwithkolin.Model.UserPost
import com.example.postappwithkolin.R
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase
import com.example.postappwithkolin.UI.Fragment.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class profileActivity : AppCompatActivity(),Post_recycler.OnItemClickListener , Post_recycler.onCommentButtonClick{

    lateinit var iv_profileImage: ImageView
    lateinit var tv_userName: TextView
    lateinit var rv_posts: RecyclerView
    lateinit var btn_sendMessage: Button
    lateinit var btn_fallBack:ImageButton

    lateinit var rv :Post_recycler

    var mAuth = FirebaseAuth.getInstance()

    var userName:String ?=null
    var userPhoto:String?=null

    var model:SAGDataFromDataBase = SAGDataFromDataBase()
    var posts:ArrayList<UserPost> = ArrayList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Initialize Views
        iv_profileImage = findViewById(R.id.profilePhoto)
        tv_userName = findViewById(R.id.profileUserName)
        rv_posts = findViewById(R.id.profilePosts)
        btn_sendMessage = findViewById(R.id.profile_send)
        btn_fallBack = findViewById(R.id.Profile_fallBack)


        btn_fallBack.setOnClickListener(View.OnClickListener {
            intent = Intent(this , MainActivity::class.java)
            startActivity(intent)
        })




        //get userName and UserPhoto from mainActivity a
        val int= intent
        userName = int.getStringExtra("UserName")
        userPhoto = int.getStringExtra("UserPhoto")
        tv_userName.text = userName
        Picasso.get().load(Uri.parse(userPhoto)).into(iv_profileImage)

        if(userName == mAuth.currentUser!!.displayName)
            btn_sendMessage.visibility = View.GONE


        model = ViewModelProvider(this).get(SAGDataFromDataBase::class.java)

        model.getPostsWitSameUserName(userName!!)

        //observer to get data that have the same UserName
        model.mutable.observe(this , Observer {
            rv = Post_recycler(it , this , this)
            rv_posts.adapter = rv
            rv_posts.layoutManager = LinearLayoutManager(this)
            rv_posts.setHasFixedSize(true)

            posts.clear()
            posts.addAll(it)
        })


        //Send Me to MessageActivity and UserName UserPhoto With Me
        btn_sendMessage.setOnClickListener(View.OnClickListener {
            val intent = Intent(this , MessageActivity::class.java)
            intent.putExtra("UserNameAccepter" ,userName)
            intent.putExtra("UserNameSender" ,mAuth.currentUser!!.displayName.toString())
            intent.putExtra("UserPhoto" , userPhoto)
            startActivity(intent)
        })
    }

    override fun onItemClick(position: Int) {
    }

    override fun onCommentClick(position: Int) {
        val intent = Intent(this, CommentActivity::class.java)

        val userName: String = posts.get(position).UserName
        val userPhoto: String = posts.get(position).UserPhoto
        val postImage: String = posts.get(position).postImage
        val postComment: String = posts.get(position).postComment

        intent.putExtra("UserName", userName)
        intent.putExtra("UserPhoto", userPhoto)
        intent.putExtra("postImage", postImage)
        intent.putExtra("postComment", postComment)
        intent.putExtra("position", position)

        startActivity(intent)
    }


}