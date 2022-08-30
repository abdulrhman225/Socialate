package com.example.postappwithkolin.SourceData

import android.util.Log
import android.view.animation.AnimationUtils
import androidx.annotation.NonNull
import androidx.browser.customtabs.PostMessageService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postappwithkolin.Model.UserPost
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class SAGDataFromDataBase : ViewModel() {

    val mutable: MutableLiveData<ArrayList<UserPost>> = MutableLiveData()

    val db = Firebase.database
    val mRef = db.reference

    lateinit var userName: String
    lateinit var comment: String
    lateinit var PostImage: String
    lateinit var UserPhoto:String


    val posts: ArrayList<UserPost> = ArrayList()


    lateinit var userPost: UserPost


    //upload all Information from UserPost Data to FireBase
    fun uploadPost(userPost: UserPost) {
       val PostKey:String =  mRef.child("Posts").push().key.toString()
        mRef.child("Posts").child(PostKey).push()
        val map: Map<String, Any?>

        map = mapOf(
            "userName" to userPost.UserName,
            "postComment" to userPost.postComment,
            "PostImage" to userPost.postImage,
            "UserPhoto" to userPost.UserPhoto
        )

        mRef.child("Posts").child(PostKey).setValue(map)
    }


    //get All Data from firebase and put it in MutableLiveData
    fun getPosts() {
        mRef.child("Posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                posts.clear()
                viewModelScope.launch(Dispatchers.IO) {

                    for (snap: DataSnapshot in snapshot.children) {
                        userName =
                            snap.child("userName").getValue().toString()
                        comment =
                            snap.child("postComment").getValue().toString()
                        PostImage =
                            snap.child("PostImage").getValue().toString()
                        UserPhoto =
                            snap.child("UserPhoto").getValue().toString()




                        userPost = UserPost(userName, comment, PostImage, UserPhoto)
                        posts.add(userPost)
                        Log.d("TAG", "onDataChange: " + PostImage)
                    }
                    withContext(Dispatchers.Main) {
                        mutable.value = posts
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: "+ error.message)
            }
        })

    }


    //get All Data with Same User Name from firebase and put it in MutableLiveData
    fun getPostsWitSameUserName(UserName:String) {
        mRef.child("Posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                for  (snap:DataSnapshot in snapshot.children) {
                    userName =
                        snap.child("userName").getValue().toString()
                    comment =
                        snap.child("postComment").getValue().toString()
                    PostImage =
                        snap.child("PostImage").getValue().toString()
                    UserPhoto =
                        snap.child("UserPhoto").getValue().toString()

                    if (userName == UserName) {

                        userPost = UserPost(userName, comment, PostImage, UserPhoto)
                        posts.add(userPost)
                    }
                }
                mutable.value = posts
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: "+ error.message)
            }
        })

    }

}


