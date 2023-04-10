package com.example.postappwithkolin.UI

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.media.session.MediaSession
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewManager
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.postappwithkolin.Model.UserInformation
import com.example.postappwithkolin.R
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

class RegisterActivity : AppCompatActivity() {

    lateinit var iv_UserImage: ImageView
    lateinit var et_UserName: TextInputEditText
    lateinit var et_Email: TextInputEditText
    lateinit var et_Password: TextInputEditText
    lateinit var btn_Register: Button
    lateinit var btn_fallback: ImageButton
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

    //Initialize Post Source
    var model: SAGDataFromDataBase = SAGDataFromDataBase()

    var Token:String ?= null




    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Initialize Views
        et_UserName = findViewById(R.id.Register_UserName)
        et_Email = findViewById(R.id.Register_Email)
        et_Password = findViewById(R.id.Register_Password)
        btn_Register = findViewById(R.id.Register_register)
        iv_UserImage = findViewById(R.id.Register_UserPhoto)
        btn_fallback = findViewById(R.id.Register_fallBack)



        model = ViewModelProvider(this).get(SAGDataFromDataBase::class.java)
        model.getAllUsers()


        //Check Permission
        permission()


        //pickImage and set It in iv_UserImage
        iv_UserImage.setOnClickListener(View.OnClickListener {
            pickImage()
        })


        //Create New User And Go To MainActivity & Save UserInformation to RealTime DataBase
        btn_Register.setOnClickListener(View.OnClickListener {
            makeNewAccount()
        })


        btn_fallback.setOnClickListener(View.OnClickListener {
            finish()
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
        }
    }


    //Add Permission
    fun permission() {
        val args = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
        )

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
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
            var dialog = AlertDialog.Builder(this).setView(R.layout.progress_wiat)
            var dia = dialog.create()
            dia.show()

            storageReference.putFile(uri!!).addOnSuccessListener(OnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    //get photo url
                    userPhoto = it.toString()
                    Log.d("TAG", "uploadPicture: " + it.toString())
                    dia.dismiss()
                }

            })

        }
    }


    //make New Account
    fun makeNewAccount() {
        val email = et_Email.text.toString()
        val password = et_Password.text.toString()
        val UserName = et_UserName.text.toString()
        val uri: String = userPhoto.toString()
        var Token:String ?= null


        //send Token and UserName
        var dialog = AlertDialog.Builder(this).setView(R.layout.progress_wiat)
        var dia :AlertDialog = dialog.create()
        dia.show()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener {
                if (it.isSuccessful) {
                    dia.dismiss()
                    Token = it.result
                }
            })

        if (email != "" && password != "" && UserName != "" && uri != ""  ) {
//                Thread.sleep(2000)

            //check if the UserName is Already exists
            if (!model.check_if_UserNameIs_exist(UserName)) {

                // Make New UserAccount
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful) {
                            //set UserName and UserPhoto to MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("userName", UserName)
                            intent.putExtra("userPhoto", userPhoto)
                            startActivity(intent)

                            //send UserInformation to Firebase
                            model.uploadUserInfo(UserInformation(UserName, email, uri))



                            model.uploadUserAndToken(UserName ,Token.toString() )
                        }

                    })
            }
            else{
                Toast.makeText(applicationContext , "UserName is Already Exists " , Toast.LENGTH_SHORT).show()
            }
        }

    }





}