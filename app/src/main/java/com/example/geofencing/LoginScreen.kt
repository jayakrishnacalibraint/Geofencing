package com.example.geofencing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.geofencing.databinding.ActivityLoginScreenBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginScreen : AppCompatActivity() {
    private lateinit var loginScreenBinding: ActivityLoginScreenBinding
    private lateinit var emailText: TextInputEditText
    private lateinit var pwdText: TextInputEditText

    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView
    private lateinit var adminLoginText: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginScreenBinding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(loginScreenBinding.root)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "     Login Screen"
        initializeViews()
        initializeFirebase()

        loginButton.setOnClickListener { signinWithEmailAddress() }
        signupTextView.setOnClickListener { navigateToRegisterScreen() }
        adminLoginText.setOnClickListener { navigateToAdminLoginScreen() }
    }




    private fun initializeFirebase() {
        auth = Firebase.auth
        database = Firebase.database.reference
    }

    private fun initializeViews() {
        emailText = loginScreenBinding.emailEdx
        pwdText = loginScreenBinding.pwdEdx
        loginButton = loginScreenBinding.loginBtn
        signupTextView = loginScreenBinding.signupText
        adminLoginText=loginScreenBinding.adminLoginTextView
    }

    private fun navigateToUserWorkScreen() {
        val intent = Intent(this, UserWork::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun navigateToRegisterScreen() {
        val intent = Intent(this, RegisterScreen::class.java)
        startActivity(intent)
    }


    private fun navigateToAdminLoginScreen() {
        val intent = Intent(this, AdminLoginScreen::class.java)
        startActivity(intent)
    }


    private fun signinWithEmailAddress() {
        email = emailText.text.toString()
        password = pwdText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "login successful", Toast.LENGTH_LONG).show()
                        navigateToUserWorkScreen()
                    } else {
                        val errorMessage = task.exception?.message
                        Toast.makeText(this, "$errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
