package com.example.geofencing.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.geofencing.user.LoginScreen
import com.example.geofencing.R
import com.example.geofencing.databinding.ActivityAdminLoginScreenBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AdminLoginScreen : AppCompatActivity() {
    private lateinit var adminLoginScreenBinding: ActivityAdminLoginScreenBinding
    private lateinit var emailText: TextInputEditText
    private lateinit var pwdText: TextInputEditText

    private lateinit var loginButton: Button
    private lateinit var userLoginTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminLoginScreenBinding = ActivityAdminLoginScreenBinding.inflate(layoutInflater)
        setContentView(adminLoginScreenBinding.root)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login Screen"
        initializeViews()
        initializeFirebase()

        loginButton.setOnClickListener { signinWithEmailAddress() }
        userLoginTextView.setOnClickListener { navigateToUserLoginScreen() }
    }




    private fun initializeFirebase() {
        auth = Firebase.auth
        database = Firebase.database.reference
    }

    private fun initializeViews() {
        emailText = adminLoginScreenBinding.emailEdx
        pwdText = adminLoginScreenBinding.pwdEdx
        loginButton = adminLoginScreenBinding.loginBtn
        userLoginTextView = adminLoginScreenBinding.userLoginTextView
    }

    private fun navigateToUserListScreen() {
        val intent = Intent(this, UsersListScreen::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun navigateToUserLoginScreen() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }

    private fun signinWithEmailAddress() {
        email = emailText.text.toString()
        password = pwdText.text.toString()
        if (email.isNotEmpty()&& password.isNotEmpty()) {
            if(email=="krishna@admin.com"){
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "login successful", Toast.LENGTH_LONG).show()
                            navigateToUserListScreen()
                        } else {
                            val errorMessage = task.exception?.message
                            Toast.makeText(this, "$errorMessage", Toast.LENGTH_LONG).show()
                        }
                    }
            }
            else if(!email.endsWith("@admin.com")){
                emailText.error = "mail should end with @admin.com"
            }

            else{
                Toast.makeText(this,"Only Admin can login ", Toast.LENGTH_SHORT).show()
            }

        }
    }
}