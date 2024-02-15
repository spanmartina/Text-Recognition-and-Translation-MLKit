package com.example.ttapp
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {
    private lateinit var editName: EditText
    private lateinit var editUsername: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnSave: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(currentUser.uid)

        val name = intent.getStringExtra("name")
        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")

        editName = findViewById(R.id.editName)
        editUsername = findViewById(R.id.editUsername)
        editPassword = findViewById(R.id.editPassword)
        btnSave = findViewById(R.id.btnSave)

        editName.setText(name)
        editUsername.setText(username)
        editPassword.setText(password)

        btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val newName = editName.text.toString().trim()
        val newUsername = editUsername.text.toString().trim()
        val newPassword = editPassword.text.toString().trim()

        if (newName.isEmpty() || newUsername.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        //Update database if the fields changed
        if (newName != intent.getStringExtra("name")) {
            databaseReference.child("name").setValue(newName)
        }
        if (newUsername != intent.getStringExtra("username")) {
            databaseReference.child("username").setValue(newUsername)
        }

        if (newPassword.isNotEmpty() && newPassword != intent.getStringExtra("password")) {
            // Re-authenticate the user
            val credential = EmailAuthProvider.getCredential(
                currentUser.email!!,
                intent.getStringExtra("password")!!
            )
            currentUser.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Update the password in Firebase Authentication
                        currentUser.updatePassword(newPassword)
                            .addOnCompleteListener { authTask ->
                                // Password updated successfully in Firebase Authentication
                                if (authTask.isSuccessful) {
                                    // Update the password in Realtime Database
                                    databaseReference.child("password").setValue(newPassword)
                                        .addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Password updated successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                // Password update failed in Realtime Database
                                                Toast.makeText(
                                                    this,
                                                    "Failed to update password in Realtime Database",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to update password in Firebase Authentication",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Re-authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        val intent = Intent(this, LandingPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}