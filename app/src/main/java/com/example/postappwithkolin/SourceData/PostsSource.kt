package com.example.postappwithkolin.SourceData

import android.content.Context
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.browser.customtabs.PostMessageService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.disklrucache.DiskLruCache
import com.example.postappwithkolin.Model.UserInformation
import com.example.postappwithkolin.Model.UserPost
import com.google.firebase.auth.UserInfo
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
    val _mutable: MutableLiveData<ArrayList<UserInformation>> = MutableLiveData()

    val db = Firebase.database
    val mRef = db.reference

    lateinit var userName_Post: String
    lateinit var comment_Post: String
    lateinit var PostImage_Post: String
    lateinit var UserPhoto_Post: String


    lateinit var userName: String
    lateinit var email: String
    lateinit var UserPhoto: String


    val posts: ArrayList<UserPost> = ArrayList()

    val Users: ArrayList<UserInformation> = ArrayList()


    lateinit var userPost: UserPost
    lateinit var UserInfo: UserInformation


    //upload all UserInformation from UserInformation to Firebase
    fun uploadUserInfo(userInfo: UserInformation) {
        val UserKey: String = mRef.child("User").push().key.toString()
        mRef.child("User").child(UserKey).push()
        val map: Map<String, Any?>

        map = mapOf(
            "userName" to userInfo.UserName,
            "email" to userInfo.email,
            "UserPhoto" to userInfo.UserPhoto
        )

        mRef.child("User").child(UserKey).setValue(map)
    }



    //get All UserInformation from Firebase
    fun getUsers() {
        mRef.child("User").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                posts.clear()
                viewModelScope.launch(Dispatchers.IO) {

                    for (snap: DataSnapshot in snapshot.children) {
                        userName =
                            snap.child("userName").getValue().toString()
                        email =
                            snap.child("email").getValue().toString()
                        UserPhoto =
                            snap.child("UserPhoto").getValue().toString()




                        UserInfo = UserInformation(userName, email, UserPhoto)
                        Users.add(UserInfo)
                    }
                    withContext(Dispatchers.Main) {
                        _mutable.value = Users
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: " + error.message)
            }
        })
    }


    //check if the UserName is Already exists
    fun check_if_UserNameIs_exist(UserName:String):Boolean{
        for (user:UserInformation in Users){
            if (UserName == user.UserName){
                return true
            }
        }
        return false
    }






    //upload all Information from UserPost Data to FireBase
    fun uploadPost(userPost: UserPost) {
        val PostKey: String = mRef.child("Posts").push().key.toString()
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
                        userName_Post =
                            snap.child("userName").getValue().toString()
                        comment_Post =
                            snap.child("postComment").getValue().toString()
                        PostImage_Post =
                            snap.child("PostImage").getValue().toString()
                        UserPhoto =
                            snap.child("UserPhoto").getValue().toString()




                        userPost =
                            UserPost(userName_Post, comment_Post, PostImage_Post, UserPhoto_Post)
                        posts.add(userPost)
                    }
                    withContext(Dispatchers.Main) {
                        mutable.value = posts
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: " + error.message)
            }
        })

    }


    //get All Data with Same User Name from firebase and put it in MutableLiveData
    fun getPostsWitSameUserName(UserName: String) {
        mRef.child("Posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                for (snap: DataSnapshot in snapshot.children) {
                    userName_Post =
                        snap.child("userName").getValue().toString()
                    comment_Post =
                        snap.child("postComment").getValue().toString()
                    PostImage_Post =
                        snap.child("PostImage").getValue().toString()
                    UserPhoto_Post =
                        snap.child("UserPhoto").getValue().toString()

                    if (userName_Post == UserName) {

                        userPost =
                            UserPost(userName_Post, comment_Post, PostImage_Post, UserPhoto_Post)
                        posts.add(userPost)
                    }
                }
                mutable.value = posts
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: " + error.message)
            }
        })

    }


}


