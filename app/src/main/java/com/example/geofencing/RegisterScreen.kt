package com.example.geofencing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.geofencing.databinding.ActivityRegisterScreenBinding
import com.example.geofencing.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterScreen : AppCompatActivity() {
    private lateinit var registerScreenBinding: ActivityRegisterScreenBinding
    private lateinit var emailText: TextInputEditText
    private lateinit var pwdText: TextInputEditText
    private lateinit var loginTextView: TextView
    private lateinit var signUpButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var email: String
    private lateinit var password: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerScreenBinding = ActivityRegisterScreenBinding.inflate(layoutInflater)
        setContentView(registerScreenBinding.root)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "     Register Screen"
        initializeViews()
        initializeFirebase()
        signUpButton.setOnClickListener { createUserWithEmailAndPassword() }
        loginTextView.setOnClickListener { navigateToLoginScreen() }

    }


    private fun initializeFirebase() {
        auth = Firebase.auth
        database = Firebase.database.reference
    }

    private fun initializeViews() {
        emailText = registerScreenBinding.emailEdx
        pwdText = registerScreenBinding.pwdEdx
        signUpButton = registerScreenBinding.signupBtn
        loginTextView = registerScreenBinding.loginText
    }

    private fun navigateToUserWorkScreen() {
        val intent = Intent(this, UserWork::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }

    private fun createUserWithEmailAndPassword() {
        email = emailText.text.toString()
        password = pwdText.text.toString()


        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        database.child("Users").child(auth.uid!!).setValue(User(auth.uid!!, email))
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this, "User created", Toast.LENGTH_LONG).show()

                                }
                            }
                        navigateToUserWorkScreen()
                    } else {
                        val errorMessage = task.exception?.message
                        Toast.makeText(this, "$errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}