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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

    val REQ_CODE: Int = 1
    var uri: Uri? = null

    lateinit var userName: String
    lateinit var userPhoto: String

    lateinit var userPost: UserPost

    //Initialize FireBase Storage
    val storage = Firebase.storage
    val storageRef = storage.reference
    var PostImageUri: String? = null


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

        Picasso.get().load(mAuth.currentUser!!.photoUrl).into(iv_UserPhoto)
        tv_UserName.text = mAuth.currentUser!!.displayName


        //send Image and Comment and Return to MainActivity
        btn_uploadPost.setOnClickListener(View.OnClickListener {
            var comment = et_Comment.text.toString()
            //save data in UserPost.class
            userPost = UserPost(mAuth.currentUser!!.displayName.toString(), comment, PostImageUri.toString(), mAuth.currentUser!!.photoUrl.toString())
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
            var dialog = AlertDialog.Builder(this).setView(R.layout.progress_wiat)
            var dia = dialog.create()
            dia.show()

            storageReference.putFile(uri!!).addOnSuccessListener(OnSuccessListener {
                //get image url
                storageReference.downloadUrl.addOnSuccessListener {
                    PostImageUri = it.toString()
                    Log.d("TAG", "uploadPicture: " + it.toString())
                    dia.dismiss()
                }

            })
        }
    }


}
