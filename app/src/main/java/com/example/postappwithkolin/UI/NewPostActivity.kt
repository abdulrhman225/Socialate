package com.example.postappwithkolin.UI

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.postappwithkolin.Model.UserPost
import com.example.postappwithkolin.R
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.runBlocking
import java.util.*

class NewPostActivity : AppCompatActivity() {

    lateinit var et_Comment: EditText
    lateinit var iv_PostImage: ImageView
    lateinit var btn_uploadPost: Button

    val REQ_CODE: Int = 1
    var uri: Uri? = null

    lateinit var userName: String
    lateinit var userPhoto: String

    lateinit var userPost: UserPost

    //Initialize FireBase Storage
    val storage = Firebase.storage
    val storageRef = storage.reference
    var PostImageUri: String? = null


    var SendPosts: SAGDataFromDataBase = SAGDataFromDataBase()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        //Initialize Views
        et_Comment = findViewById(R.id.NPA_et_comment)
        iv_PostImage = findViewById(R.id.NPA_IV_PostImage)
        btn_uploadPost = findViewById(R.id.NPA_btn_uploadPost)


        //get 1-userName 2-UserPhoto From MainActivity
        val intent = intent
        userName = intent.getStringExtra("userName").toString()
        userPhoto = intent.getStringExtra("userPhoto").toString()

        //send Image and Comment and Return to MainActivity
        btn_uploadPost.setOnClickListener(View.OnClickListener {
            var comment = et_Comment.text.toString()
            //save data in UserPost.class
            userPost = UserPost(userName, comment, PostImageUri.toString(), userPhoto)
            SendPosts.uploadPost(userPost)

            finish()

        })

        iv_PostImage.setOnClickListener(View.OnClickListener {
            //choose image from gallery
            pickImage()
        })
    }


    //Pick Image From Gallery
    fun pickImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, REQ_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
            uri = data!!.data!!
            Picasso.get().load(uri).into(iv_PostImage)

            //upload PostImage
            uploadPicture()
            //give system time to get the image from firebase
            Thread.sleep(5000)

        }
    }

    //get FireExtension like (jpg or png)
    fun getFileExtension(uri: Uri): String {
        val cR: ContentResolver = getContentResolver();
        val mime: MimeTypeMap = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri))!!;
    }

    //upload Picture to FireBase Storage
    fun uploadPicture() {
        var imagePath: String? = null

        if (uri != null) {

            val storageReference: StorageReference = storageRef.child(
                "images/${System.currentTimeMillis()}.${
                    getFileExtension(
                        uri!!
                    )
                }"
            )

            storageReference.putFile(uri!!).addOnSuccessListener(OnSuccessListener {
                //get image url
                storageReference.downloadUrl.addOnSuccessListener {
                    PostImageUri = it.toString()
                    Log.d("TAG", "uploadPicture: " + it.toString())
                }

            })
        }
    }


}
