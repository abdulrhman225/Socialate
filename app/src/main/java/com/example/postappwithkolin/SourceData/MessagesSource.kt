package com.example.postappwithkolin.SourceData

import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postappwithkolin.Model.UserMessages
import com.example.postappwithkolin.Model.UserPost
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.cache.DiskLruCache

class MessagesSource : ViewModel() {
    val massage: MutableLiveData<ArrayList<UserMessages>> = MutableLiveData()

    val db = Firebase.database
    val mRef = db.reference

    lateinit var userNameSender: String
    lateinit var userNameAccepter: String
    lateinit var all_Message: String


    val messages: ArrayList<UserMessages> = ArrayList()


    lateinit var usersMessage: UserMessages


    //upload all Information from UserMessages Data  to FireBase
    fun uploadMessage(userMessage: UserMessages) {
        val MessageKey: String = mRef.child("Message").push().key.toString()
        mRef.child("Message").child(MessageKey).push()
        val map: Map<String, Any?>

        map = mapOf(
            "userNameSender" to userMessage.SenderUser,
            "UserNameAccepter" to userMessage.accepterUser,
            "messages" to userMessage.Message,
        )

        mRef.child("Message").child(MessageKey).setValue(map)
    }


    //get All Data from firebase and put it in MutableLiveData
    fun getMessages(UserNameSender: String, UserNameAccepter: String) {
        mRef.child("Message").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (snap in snapshot.children) {
                    userNameSender =
                        snap.child("userNameSender").getValue().toString()
                     userNameAccepter =
                        snap.child("UserNameAccepter").getValue().toString()
                     all_Message =
                        snap.child("messages").getValue().toString()

                    // give me all Message by  userNameSender and UserNameAccepter
                    if (UserNameSender == userNameSender && UserNameAccepter == userNameAccepter

                    ) {
                        usersMessage = UserMessages(userNameSender, userNameAccepter, all_Message)
                        messages.add(usersMessage)
                        Log.d("TAG", "onDataChange: " + all_Message)
                    } else if (UserNameSender == userNameAccepter && UserNameAccepter == userNameSender
                    ) {

                        usersMessage = UserMessages(userNameSender, userNameAccepter, all_Message)
                        messages.add(usersMessage)
                        Log.d("TAG", "onDataChange: " + all_Message)
                    }
                }
                massage.value = messages

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}



