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
import com.google.firebase.auth.UserProfileChangeRequest
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
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    lateinit var iv_UserImage: ImageView
    lateinit var et_UserName: TextInputEditText
    lateinit var et_Email: TextInputEditText
    lateinit var et_Password: TextInputEditText
    lateinit var et_ConfirmPassword: TextInputEditText
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
        et_ConfirmPassword = findViewById(R.id.Register_ConfirmPassword)
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
        val confirmPassword = et_ConfirmPassword.text.toString()
        val UserName = et_UserName.text.toString()
        val uri: String = userPhoto.toString()
        var Token:String ?= null

        val emailRegex = "^(.+)@(.+)\$"
        val pattern:Pattern = Pattern.compile(emailRegex)
        val matcher: Matcher = pattern.matcher(email)


        val passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$"
        val pat:Pattern = Pattern.compile(passwordRegex)
        val mat: Matcher = pattern.matcher(password)


        val CheckingIfUserNameIsUnique = !model.check_if_UserNameIs_exist(UserName)
        val CheckIfPersonalFiledIsNotEmpty = email != "" && password != "" && UserName != "" && uri !="";



        var dia = waitingAlertDialog()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener {
                if (it.isSuccessful) {
                    dia.dismiss()
                    Token = it.result
                }
            })

        if (CheckIfPersonalFiledIsNotEmpty) {
            if(confirmPassword != password) {
                if (CheckingIfUserNameIsUnique) {
                    if (matcher.matches()) {
                        if (mat.matches()) {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(
                                    OnCompleteListener {
                                        if (it.isSuccessful) {

                                            updateUserProfile()
                                            model.uploadUserInfo(
                                                UserInformation(
                                                    UserName,
                                                    email,
                                                    uri
                                                )
                                            )
                                            model.uploadUserAndToken(UserName, Token.toString())
                                        }

                                    })
                        } else {
                            et_Password.error =
                                "your password should contain at least 8 digits ,one simple ,one Capital letter and one Small letter "
                        }
                    } else {
                        et_Email.error = "Please Enter Correct Email"
                    }
                } else {
                    et_UserName.error = "This User Name is already Taken"
                }
            }else{
                et_ConfirmPassword.error = "the Password and Confirm Password does not match"
            }
        }else{
            checkingIsThereAnyFieldEmpty()
        }

    }

    private fun waitingAlertDialog() :AlertDialog{
        var dialog = AlertDialog.Builder(this).setView(R.layout.progress_wiat)
        var dia :AlertDialog = dialog.create()
        dia.show()

        return dia;
    }

    private fun checkingIsThereAnyFieldEmpty(){
        val email = et_Email.text.toString()
        val password = et_Password.text.toString()
        val UserName = et_UserName.text.toString()
        val uri: String = userPhoto.toString()

        if(email == ""){
            et_Email.error = "Please Fill This Field"
        }

        if(password == ""){
            et_Password.error = "Please Fill This Field"
        }

        if(UserName == ""){
            et_UserName.error = "Please Fill This Field"
        }

        if(uri == ""){
            Toast.makeText(applicationContext , "Please Choose Photo" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserProfile(){
        val UserName = et_UserName.text.toString()

        val user = mAuth.currentUser
        val updateProfile =
            UserProfileChangeRequest.Builder().setDisplayName(
                UserName
            )
                .setPhotoUri(Uri.parse(userPhoto)).build()

        user!!.updateProfile(updateProfile).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }





}