package com.example.ttapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {

    private lateinit var signupName: EditText
    private lateinit var signupUsername: EditText
    private lateinit var signupEmail: EditText
    private lateinit var signupPassword: EditText
    private lateinit var signupConfirmPassword: EditText
    private lateinit var redirectText: TextView
    private lateinit var signupButton: Button

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signupName = findViewById(R.id.signup_name)
        signupEmail = findViewById(R.id.signup_email)
        signupUsername = findViewById(R.id.signup_username)
        signupPassword = findViewById(R.id.signup_password)
        signupConfirmPassword = findViewById(R.id.signup_confirm_password)
        redirectText = findViewById(R.id.redirectText)
        signupButton = findViewById(R.id.signup_button)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.reference.child("users")

        signupButton.setOnClickListener {
            val name = signupName.text.toString()
            val email = signupEmail.text.toString()
            val username = signupUsername.text.toString()
            val password = signupPassword.text.toString()

            if(validateCredentials()){
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser

                            // Save additional user data to Realtime Database
                            val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                            val userKey = user?.uid

                            if (userKey != null) {
                                // Create an AuthHelper object with additional user data
                                val authHelper = AuthHelper(name, email, username, password)

                                // Save AuthHelper data to Realtime Database under the user's uid
                                reference.child(userKey).setValue(authHelper)
                                    .addOnSuccessListener {
                                        showToast("Successful Sign Up!")
                                        val intent = Intent(this@SignUpActivity, LandingPage::class.java)
                                        startActivity(intent)
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        showToast("Failed to Sign Up!")
                                    }
                            }
                        } else {
                            showToast("Failed to create user: ${task.exception?.message}")
                        }
                    }.addOnFailureListener { exception ->
                        if (exception is FirebaseAuthUserCollisionException) {
                            Log.e("!!Email is already in use","Email is already in use.")
                        } else {
                            Log.e("!!Email is already in use"," ${exception.message}")

                            showToast("Failed to create user: ${exception.message}")
                        }
                    }
            }
        }
        redirectText.setOnClickListener {
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    private fun validateCredentials(): Boolean {
        val name = signupName.text.toString().trim()
        val email = signupEmail.text.toString().trim()
        val username = signupUsername.text.toString().trim()
        val password = signupPassword.text.toString().trim()
        val confirmPassword = signupConfirmPassword.text.toString().trim()

        if (name.isEmpty()) {
            signupName.error = "Name is required!"
            return false
        }

        if (email.isEmpty()) {
            signupEmail.error = "Email is required!"
            return false
        }

        if (username.isEmpty()) {
            signupUsername.error = "Username is required!"
            return false
        }

        if (password.isEmpty()) {
            signupPassword.error = "Password is required!"
            signupConfirmPassword.error = "Confirm Password is required!"
            return false
        }

        if (password != confirmPassword) {
            signupConfirmPassword.error = "Passwords do not match!"
            return false
        }

        return true
    }
    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}