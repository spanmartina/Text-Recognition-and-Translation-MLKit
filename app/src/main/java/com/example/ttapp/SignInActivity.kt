package com.example.ttapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class SignInActivity : AppCompatActivity() {

    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var redirectText: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        loginEmail = findViewById(R.id.login_email)
        loginPassword = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        redirectText = findViewById(R.id.redirectText)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            if (validateUsername() and validatePassword()) {
                signInUser()
            }
        }

        redirectText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    private fun validateUsername(): Boolean {
        val valText =  loginEmail.text.toString()
        return if (valText.isEmpty()) {
            loginEmail.error = "Email field cannot be empty"
            false
        } else {
            loginEmail.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val valText = loginPassword.text.toString()
        return if (valText.isEmpty()) {
            loginPassword.error = "Password cannot be empty"
            false
        } else {
            loginPassword.error = null
            true
        }
    }

    private fun signInUser() {
        val email = loginEmail.text.toString().trim()
        val password = loginPassword.text.toString().trim()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, navigate to the landing page
                    val intent = Intent(this@SignInActivity, LandingPage::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            loginEmail.error = "User not found"
                            loginEmail.requestFocus()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            loginPassword.error = "Invalid password"
                            loginPassword.requestFocus()
                        }
                        else -> {
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }
}