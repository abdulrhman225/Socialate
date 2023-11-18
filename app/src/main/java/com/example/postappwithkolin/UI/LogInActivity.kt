package com.example.postappwithkolin.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.postappwithkolin.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern


class LogInActivity : AppCompatActivity() {

    lateinit var et_Email: TextInputEditText
    lateinit var et_Password: TextInputEditText
    lateinit var tv_Go_To_Register: TextView
    lateinit var btn_login: Button

    //Initialize FireBaseAuth
    val mAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        //Initialize Views
        et_Email = findViewById(R.id.Login_Email)
        et_Password = findViewById(R.id.Login_Password)
        tv_Go_To_Register = findViewById(R.id.Login_GoToRegister)
        btn_login = findViewById(R.id.Login_login)


        //Go To RegisterActivity To Make New Account
        tv_Go_To_Register.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        })


        //signIn and Go To MainActivity
        btn_login.setOnClickListener(View.OnClickListener {
            val email = et_Email.text.toString()
            val password = et_Password.text.toString()

            val regex = "^(.+)@(.+)\$"
            val pattern:Pattern = Pattern.compile(regex)
            val matcher: Matcher = pattern.matcher(email)

            if(matcher.matches()) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                "Log In Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                        }
                        else {
                            et_Email.error = "Your email address or password is not correct"
                        }
                    })
            }
            else{
                et_Email.error = "Please Enter Correct Email"
            }

        })
    }
}