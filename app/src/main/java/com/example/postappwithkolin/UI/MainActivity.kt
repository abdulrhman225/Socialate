package com.example.postappwithkolin.UI

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity(), HomeFragment.OnItemClickListener1,
    SearchFragment.onChangeListener1, users_rv.OnCompleteListener,
    SearchFragment.onCompleteListener1, SettingFragment.onLogOutClickListener,
    SettingFragment.onChangeUserNameClickListener,
    SettingFragment.onChangeProfileImageClickListener {


    //    lateinit var iv_UserImage: ImageView
//    lateinit var tv_UserName: TextView
//    lateinit var main_rv: RecyclerView
    lateinit var FAB_newPost: FloatingActionButton

    //    lateinit var swipe:SwipeRefreshLayout
    lateinit var bottomNavigation: BottomNavigationView
    lateinit var Frame: FrameLayout


    //Initialize FireBaseAuth
    val mAuth = Firebase.auth

    //Initialize RecyclerView
    var rv: Post_recycler? = null
    var Rv: users_rv? = null

    companion object {
        var userName: String? = null

        var userPhoto: String? = null
    }

    var userPhotoInPost: String? = null

    var posts: ArrayList<UserPost> = ArrayList()
    var newList: ArrayList<UserInformation> = ArrayList()


    //Initialize SAGDataFromFireBase
    var model: SAGDataFromDataBase = SAGDataFromDataBase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize Views
//        iv_UserImage = findViewById(R.id.main_IV_userImage)
//        tv_UserName = findViewById(R.id.main_UserName)
//        main_rv = findViewById(R.id.Main_Recycler)
        FAB_newPost = findViewById(R.id.main_FLB)
//        swipe = findViewById(R.id.MainSwipe)
        bottomNavigation = findViewById(R.id.Main_bottomNavigation)
        Frame = findViewById(R.id.Main_frame)




        bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.search -> replaceFragment(SearchFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.setting -> replaceFragment(SettingFragment())
            }
            true
        })


        //put UserName And UserPhoto in the MainActivity
        putUserNameAndUserphoto()

        //observer to updated data
        model = ViewModelProvider(this).get(SAGDataFromDataBase::class.java)
//        updateData()


//        Going to NewPostActivity
        FAB_newPost.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, NewPostActivity::class.java)
            intent.putExtra("userName", mAuth.currentUser!!.displayName)
            intent.putExtra("userPhoto", mAuth.currentUser!!.photoUrl)
            startActivity(intent)
        })

        //swipe to update data
//        swipe.setOnRefreshListener {
//            updateData()
//            swipe.isRefreshing = false
//        }

    }


//    //Create Option Menu
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater: MenuInflater = MenuInflater(this)
//        inflater.inflate(R.menu.log_out_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//
//    }


    //Initialize menuItem for signOut
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        when (item.itemId) {
//            R.id.menu_Logout -> {
//                mAuth.signOut()
//                val intent = Intent(this, LogInActivity::class.java)
//                startActivity(intent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//
    //Checking If there is User Or Not if Not Her Will Send Me To LoginActivity
    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser == null) {
            var intent = Intent(applicationContext, LogInActivity::class.java)
            startActivity(intent)
        } else {
            userPhotoInPost = mAuth.currentUser!!.photoUrl.toString()
            replaceFragment(HomeFragment())
        }
    }


    //get UserName and UserPhoto from Register Activity and update the UserInformation
    fun putUserNameAndUserphoto() {
        val intent = getIntent()
        userName = intent.getStringExtra("userName")
        userPhoto = intent.getStringExtra("userPhoto")

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

    override fun onChangeUserName(UserName: String?) {
        TODO("Not yet implemented")
    }

    override fun onChangeProfileImage() {
        TODO("Not yet implemented")
    }

    override fun onLogOut() {
        TODO("Not yet implemented")
    }


}