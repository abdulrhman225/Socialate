package com.example.postappwithkolin.UI

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.postappwithkolin.Model.Post_recycler
import com.example.postappwithkolin.Model.UserInformation
import com.example.postappwithkolin.Model.UserPost
import com.example.postappwithkolin.Model.users_rv
import com.example.postappwithkolin.R
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase
import com.example.postappwithkolin.UI.Fragment.HomeFragment
import com.example.postappwithkolin.UI.Fragment.ProfileFragment
import com.example.postappwithkolin.UI.Fragment.SearchFragment
import com.example.postappwithkolin.UI.Fragment.SettingFragment
import com.example.postappwithkolin.Room.Post.*
import com.example.postappwithkolin.Room.Users.UsersDataBase
import com.example.postappwithkolin.Room.Users.resultUsers
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity(), HomeFragment.OnItemClickListener1,
    SearchFragment.onChangeListener1, users_rv.OnCompleteListener,
    SearchFragment.onCompleteListener1, SettingFragment.onLogOutClickListener,
    SettingFragment.onChangeUserNameClickListener,
    SettingFragment.onChangeProfilePhotoClickListener, HomeFragment.onCommentClick {

    val TAG = "MainActivity"

    lateinit var FAB_newPost: FloatingActionButton

    lateinit var bottomNavigation: BottomNavigationView
    lateinit var Frame: FrameLayout

    val REQ_CODE = 1
    var uri: Uri? = null
    lateinit var iv_UserPhoto: ImageView

    //userPhotoPath
    var ProfilePhoto: String? = null


    //Initialize FireBase Storage
    val storage = Firebase.storage
    val storageRef = storage.reference


    //change UserName AlertDialog
    lateinit var builder: AlertDialog.Builder
    lateinit var dialgo: AlertDialog

    //newPost Alert Dialog
    lateinit var newBuilder: AlertDialog.Builder
    lateinit var newDialog: AlertDialog

    //Initialize FireBaseAuth
    val mAuth = Firebase.auth

    //Initialize RecyclerView
    var rv: Post_recycler? = null
    var Rv: users_rv? = null


    var posts: ArrayList<UserPost> = ArrayList()
    var newList: ArrayList<UserInformation> = ArrayList()


    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var userResult: resultUsers
        @SuppressLint("StaticFieldLeak")
        lateinit var database:postDataBase
        lateinit var db:UsersDataBase

        var isConnected:Boolean = true
    }

    //Initialize SAGDataFromFireBase
    var model: SAGDataFromDataBase = SAGDataFromDataBase()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Initialize Views
        bottomNavigation = findViewById(R.id.Main_bottomNavigation)
        Frame = findViewById(R.id.Main_frame)
        FAB_newPost = findViewById(R.id.main_FLB)

        bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.search -> replaceFragment(SearchFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.setting -> replaceFragment(SettingFragment())
            }
            true
        })


        database= postDataBase.getInstance(this)
        db = UsersDataBase.getInstance(this)
        userResult = resultUsers(this)

        getPostDataFromFireBaseAndPutItIntoRoom()
        getUserDataFromFireBaseAndPutItIntoRoom()
        checkIfIAmConnectingToNetwork()




//        Going to NewPostActivity
        FAB_newPost.setOnClickListener(View.OnClickListener {
            if(isConnected) {
                val intent = Intent(applicationContext, NewPostActivity::class.java)
                intent.putExtra("userName", mAuth.currentUser!!.displayName)
                intent.putExtra("userPhoto", mAuth.currentUser!!.photoUrl)
                startActivity(intent)
            }
        })


    }


    //Checking If there is User Or Not if Not Her Will Send Me To LoginActivity
    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser == null) {
            var intent = Intent(applicationContext, LogInActivity::class.java)
            startActivity(intent)
        } else {
            replaceFragment(HomeFragment())

        }
    }


    fun replaceFragment(fragment: Fragment?) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.Main_frame, fragment!!)
        transaction.commit()
    }

    override fun onItemClick1(Position: Int) {
        val user: UserPost = HomeFragment.posts[Position]
        val intent = Intent(this, profileActivity::class.java)
        intent.putExtra("UserName", user.UserName)
        intent.putExtra("UserPhoto", user.UserPhoto)
        startActivity(intent)
    }

    override fun onChange(newText: String?, informations: java.util.ArrayList<UserInformation>?) {
        newList.clear()
        for (information: UserInformation in informations!!) {
            if (information.UserName.lowercase().contains(newText!!.lowercase())) {
                newList.add(information)
            }
        }
        if (newList.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "There is no user Name Like " + newText,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Rv = users_rv(newList, this)
            SearchFragment.recyclerView.adapter = Rv
            SearchFragment.recyclerView.layoutManager = LinearLayoutManager(this)
            SearchFragment.recyclerView.setHasFixedSize(true)
        }
    }

    override fun onComplete(position: Int) {
        val users: UserInformation = newList[position]
        val intent = Intent(this, profileActivity::class.java)
        intent.putExtra("UserName", users.UserName)
        intent.putExtra("UserPhoto", users.UserPhoto)
        startActivity(intent)
    }

    override fun onComplete(position: Int, informations: java.util.ArrayList<UserInformation>?) {
        val users: UserInformation = informations!![position]
        val intent = Intent(this, profileActivity::class.java)
        intent.putExtra("UserName", users.UserName)
        intent.putExtra("UserPhoto", users.UserPhoto)
        startActivity(intent)
    }


    override fun onLogOut() {
        mAuth.signOut()
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
    }

    override fun changeUserName() {
        val view: View =
            LayoutInflater.from(this).inflate(R.layout.custom_dialog_chang_user_name, null, false)
        val et_NewUserName: TextInputEditText = view.findViewById(R.id.ChangeUserName)
        val btn_cancel: Button = view.findViewById(R.id.custom_dialog_Cancel)
        val btn_save: Button = view.findViewById(R.id.custom_dialog_changUserName_Save)

        et_NewUserName.setText(mAuth.currentUser!!.displayName)
        btn_save.setOnClickListener(OnClickListener {
            if (!model.check_if_UserNameIs_exist(et_NewUserName.getText().toString())) {

                model.updateUserName(
                    mAuth.currentUser!!.displayName.toString(),
                    et_NewUserName.getText().toString()
                )

                var profile = userProfileChangeRequest {
                    displayName = et_NewUserName.getText().toString()
                }
                mAuth.currentUser!!.updateProfile(profile).addOnCompleteListener {
                    if (it.isSuccessful) {
                        dialgo.cancel()
                        replaceFragment(SettingFragment())
                    }
                }

            } else {
                Toast.makeText(
                    applicationContext,
                    "the user Name is already exists",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        btn_cancel.setOnClickListener(OnClickListener {
            dialgo.cancel()
        })

        builder = AlertDialog.Builder(this).setView(view)
        dialgo = builder.create()
        dialgo.show()

    }

    override fun ChangeProfilePhoto() {

        val view: View =
            LayoutInflater.from(this).inflate(R.layout.custom_dialog_change_user_photo, null, false)
        iv_UserPhoto = view.findViewById(R.id.custom_newUserPhoto)
        val btn_cancel: Button = view.findViewById(R.id.custom_dialog_Cancel)
        val btn_save: Button = view.findViewById(R.id.custom_dialog_changUserName_Save)

        iv_UserPhoto.setOnClickListener(OnClickListener {
            pickImage()
        })


        btn_save.setOnClickListener(OnClickListener {
            model.updateUserPhoto(mAuth.currentUser!!.displayName.toString(), ProfilePhoto!!)

            var profile = userProfileChangeRequest {
                photoUri = Uri.parse(ProfilePhoto)
            }
            mAuth.currentUser!!.updateProfile(profile).addOnCompleteListener {
                if (it.isSuccessful) {
                    dialgo.cancel()
                    replaceFragment(SettingFragment())
                }
            }
        })

        btn_cancel.setOnClickListener(OnClickListener {
            dialgo.cancel()
        })

        builder = AlertDialog.Builder(this).setView(view)
        dialgo = builder.create()
        dialgo.show()
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
            Picasso.get().load(uri).into(iv_UserPhoto)

            replaceFragment(SettingFragment())


            //upload Picture
            uploadPicture()
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
            var dialog = android.app.AlertDialog.Builder(this).setView(R.layout.progress_wiat)
            var dia = dialog.create()
            dia.show()

            storageReference.putFile(uri!!).addOnSuccessListener(OnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    //get photo url
                    ProfilePhoto = it.toString()
                    Log.d("TAG", "uploadPicture: " + it.toString())
                    dia.dismiss()
                }

            })

        }
    }

    override fun onCommentclick(position: Int) {

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

    fun getPostDataFromFireBaseAndPutItIntoRoom(){
        //observer to updated data
        model = ViewModelProvider(this).get(SAGDataFromDataBase::class.java)
        //get the data from the data source
        model.getPost()
        model.mutable.observe(this, Observer {

            if(!it.isEmpty()) {
                posts.clear()
                posts.addAll(it)
            }


        })
    }

    fun getUserDataFromFireBaseAndPutItIntoRoom(){
        //observer to updated data
        model = ViewModelProvider(this).get(SAGDataFromDataBase::class.java)
        //get the data from the data source
        model.getAllUsers()
        model._mutable.observe(this, Observer {
            userResult.InsertIntoUserRoom(it)

        })




    }

    fun checkIfIAmConnectingToNetwork(){
        val manager:ConnectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val info: NetworkInfo? = manager.activeNetworkInfo;
        isConnected = info !=null && info.isConnectedOrConnecting
    }


}