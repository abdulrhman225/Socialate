package com.example.postappwithkolin.UI

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.postappwithkolin.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class RegisterActivity : AppCompatActivity() {

    lateinit var iv_UserImage: ImageView
    lateinit var et_UserName: EditText
    lateinit var et_Email: EditText
    lateinit var et_Password: EditText
    lateinit var btn_Register: Button
    val REQ_CODE = 1


    var uri: Uri? = null

    //Initialize FireBaseAuth
    val mAuth = Firebase.auth

    //Initialize RealTime DataBase
    val db = Firebase.database
    val mRef = db.reference

    //Initialize FireBase Storage
    val storage = Firebase.storage
    val storageRef = storage.reference

    //userPhotoPath
    var userPhoto: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Initialize Views
        et_UserName = findViewById(R.id.Register_UserName)
        et_Email = findViewById(R.id.Register_Email)
        et_Password = findViewById(R.id.Register_Password)
        btn_Register = findViewById(R.id.Register_register)
        iv_UserImage = findViewById(R.id.Register_UserPhoto)


        //Check Permission
        permission()


        //pickImage and set It in iv_UserImage
        iv_UserImage.setOnClickListener(View.OnClickListener {
            pickImage()
        })


        //Create New User And Go To MainActivity & Save UserInformation to RealTime DataBase
        btn_Register.setOnClickListener(View.OnClickListener {

            val email = et_Email.text.toString()
            val password = et_Password.text.toString()
            val UserName = et_UserName.text.toString()
            val uri: String = uri.toString()
            if (et_Email != null && et_Password != null && et_UserName != null && iv_UserImage != null) {


                // Make New UserAccount
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful) {
                            //set UserName and UserPhoto to MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("userName", UserName)
                            intent.putExtra("userPhoto", userPhoto)
                            startActivity(intent)
                        }

                    })
            }
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
            Picasso.get().load(uri).into(iv_UserImage)

            //upload Picture
            uploadPicture()
            //give system time to get the image from firebase
            Thread.sleep(5000)
        }
    }


    //Add Permission
    fun permission() {
        val args = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(args, 1)
            }
        }
    }


    //getFile Extension like (jpg , png)
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
                storageReference.downloadUrl.addOnSuccessListener {
                    //get photo url
                    userPhoto = it.toString()
                    Log.d("TAG", "uploadPicture: " + it.toString())
                }

            })

        }
    }


}