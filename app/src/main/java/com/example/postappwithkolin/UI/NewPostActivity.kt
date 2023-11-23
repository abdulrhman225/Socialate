package com.example.postappwithkolin.UI

import android.app.AlertDialog
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
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import com.example.postappwithkolin.Model.UserPost
import com.example.postappwithkolin.R
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.runBlocking
import java.util.*

class NewPostActivity : AppCompatActivity() {

    lateinit var et_Comment: EditText
    lateinit var iv_PostImage: ImageView
    lateinit var btn_uploadPost: Button
    lateinit var tv_UserName: TextView
    lateinit var iv_UserPhoto: CircleImageView
    lateinit var btn_uploadImage: Button
    lateinit var btn_uploadVideo: Button
    lateinit var vv_PostVideo: VideoView
    lateinit var FL_video: FrameLayout

    val IV_REQ_CODE: Int = 1
    val VV_REQ_CODE: Int = 2
    var uri: Uri? = null

    lateinit var userName: String
    lateinit var userPhoto: String

    lateinit var userPost: UserPost

    //Initialize FireBase Storage
    val storage = Firebase.storage
    val storageRef = storage.reference
    var PostImageUri : String? = null
    var postVideoUri : String? = null


    //Initialize FireBase Auth
    val mAuth:FirebaseAuth = FirebaseAuth.getInstance()

    var SendPosts: SAGDataFromDataBase = SAGDataFromDataBase()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        //Initialize Views
        et_Comment = findViewById(R.id.NPA_et_comment)
        iv_PostImage = findViewById(R.id.NPA_IV_PostImage)
        btn_uploadPost = findViewById(R.id.NPA_btn_uploadPost)
        tv_UserName = findViewById(R.id.NPA_UserName)
        iv_UserPhoto = findViewById(R.id.NPA_userPhoto)
        btn_uploadVideo = findViewById(R.id.uploadVideo)
        btn_uploadImage = findViewById(R.id.uploadImage)
        vv_PostVideo = findViewById(R.id.NPA_IV_PostVideo)
        FL_video = findViewById(R.id.frameLayoutForVideo)

        Picasso.get().load(mAuth.currentUser!!.photoUrl).into(iv_UserPhoto)
        tv_UserName.text = mAuth.currentUser!!.displayName


        val mc = MediaController(this)
        vv_PostVideo.setMediaController(mc)
        mc.setAnchorView(vv_PostVideo)

        btn_uploadPost.setOnClickListener(View.OnClickListener {
            val comment = et_Comment.text.toString()
            //save data in UserPost.class
            userPost = UserPost(mAuth.currentUser!!.displayName.toString(), comment, PostImageUri.toString() ,postVideoUri.toString() ,  mAuth.currentUser!!.photoUrl.toString())
            SendPosts.uploadPost(userPost)

            finish()

        })


        btn_uploadImage.setOnClickListener {
            vv_PostVideo.visibility = View.GONE
            pickImage()
        }

        btn_uploadVideo.setOnClickListener {
            iv_PostImage.visibility = View.GONE
            pickVideo()
        }
    }


    fun pickImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, IV_REQ_CODE)
    }


    fun pickVideo(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "video/*"
        startActivityForResult(intent, VV_REQ_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IV_REQ_CODE && resultCode == RESULT_OK) {
            uri = data!!.data!!
            Picasso.get().load(uri).into(iv_PostImage)
            uploadPicture()
            iv_PostImage.visibility = View.VISIBLE

        }
        else if(requestCode == VV_REQ_CODE && resultCode == RESULT_OK){
            uri = data!!.data!!
            vv_PostVideo.setVideoURI(uri)
            FL_video.visibility = View.VISIBLE
            vv_PostVideo.start()
            uploadVideo()
        }
    }

    fun getFileExtension(uri: Uri): String {
        val cR: ContentResolver = getContentResolver();
        val mime: MimeTypeMap = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri))!!;
    }

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
            val dialog = AlertDialog.Builder(this).setView(R.layout.progress_wiat)
            val dia = dialog.create()
            dia.show()

            storageReference.putFile(uri!!).addOnSuccessListener(OnSuccessListener {
                //get image url
                storageReference.downloadUrl.addOnSuccessListener {
                    PostImageUri = it.toString()
                    postVideoUri = null
                    dia.dismiss()
                }

            })
        }
    }

    fun uploadVideo(){
        if (uri != null) {

            val storageReference: StorageReference = storageRef.child(
                "videos/${System.currentTimeMillis()}.${
                    getFileExtension(
                        uri!!
                    )
                }"
            )
            val dialog = AlertDialog.Builder(this).setView(R.layout.progress_wiat)
            val dia = dialog.create()
            dia.show()

            storageReference.putFile(uri!!).addOnSuccessListener(OnSuccessListener {
                //get image url
                storageReference.downloadUrl.addOnSuccessListener {
                    postVideoUri = it.toString()
                    PostImageUri = null
                    dia.dismiss()
                }

            })
        }
    }

}
